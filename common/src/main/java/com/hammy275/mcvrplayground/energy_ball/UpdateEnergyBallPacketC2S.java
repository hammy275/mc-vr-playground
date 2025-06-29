package com.hammy275.mcvrplayground.energy_ball;

import com.hammy275.mcvrplayground.MCVRPlayground;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;

import java.util.Optional;

/**
 * Packet for updating the state of an energy ball. Sent while the energy ball item is being used.
 * <br>
 * Although the server has access to historical VR information, thus making the client-->server communication initially
 * seem redundant, giving the client primary control has a couple advantages:
 * <ul>
 *     <li>The client dictating the position or release velocity helps to make the energy ball feel nicer, as
 *         position or velocity data doesn't need to be sent by the server to the client. Instead, the client can perform
 *         the action immediately and update the server on the new state.</li>
 *     <li>Historical VR data from any source other than the client about the local player suffers from potential
 *         issues surrounding network latency, such as the same data being used for multiple ticks of history due
 *         to the server not receiving new data due to network lag.</li>
 * </ul>
 *
 * @param vec The position of the energy ball if state is {@link State#GROW} or {@link State#NO_GROW}, or the velocity
 *            to shoot the ball at if state is {@link State#SHOOT}.
 * @param state The state to update the energy ball to.
 */
public record UpdateEnergyBallPacketC2S(Vec3 vec, State state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateEnergyBallPacketC2S> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MCVRPlayground.MOD_ID, "update_energy_ball"));

    public static final StreamCodec<FriendlyByteBuf, UpdateEnergyBallPacketC2S> STREAM_CODEC = StreamCodec.composite(
            Vec3.STREAM_CODEC, UpdateEnergyBallPacketC2S::vec,
            State.STREAM_CODEC, UpdateEnergyBallPacketC2S::state,
            UpdateEnergyBallPacketC2S::new
    );

    public static void handle(UpdateEnergyBallPacketC2S packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            Player ctxPlayer = context.getPlayer();
            // Client sent us a packet to update or create an energy ball and they're in VR.
            if (ctxPlayer instanceof ServerPlayer player && VRAPI.instance().isVRPlayer(player)) {
                // Check that the new ball position is within a reasonable distance of the player.
                if (packet.vec.distanceToSqr(player.getEyePosition()) > 3 * 3 && packet.state != State.SHOOT) {
                    return;
                }
                // Find a nearby ball that this player is the owner of
                Optional<EnergyBallEntity> ballOpt = EnergyBallEntity.getNearbyBall(player);

                if (ballOpt.isEmpty()) {
                    // No ball found, make a new one!
                    if (packet.state != State.SHOOT) {
                        EnergyBallEntity.createFromVRPlayer(player, packet.vec);
                    }
                } else {
                    // Ball found. Update it if not shot, or do nothing if it has been.
                    EnergyBallEntity ball = ballOpt.get();
                    if (!ball.energyBallShot()) {
                        if (packet.state == State.SHOOT) {
                            ball.shoot(packet.vec);
                        } else {
                            ball.moveTo(packet.vec);
                            if (packet.state == State.GROW) {
                                ball.grow();
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public enum State {
        NO_GROW, // Used when the player is still growing the energy ball but isn't moving their hands fast enough.
        GROW, // Used when the player is growing the energy ball and is moving their hands fast enough.
        SHOOT; // Used to denote the energy ball should be shot (released from player control).

        public static final StreamCodec<FriendlyByteBuf, State> STREAM_CODEC = StreamCodec.of(
                (buffer, state) -> buffer.writeInt(state.ordinal()),
                buffer -> State.values()[buffer.readInt()]
        );
    }
}
