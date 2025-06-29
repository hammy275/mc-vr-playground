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
        NO_GROW, GROW, SHOOT;

        public static final StreamCodec<FriendlyByteBuf, State> STREAM_CODEC = StreamCodec.of(
                (buffer, state) -> buffer.writeInt(state.ordinal()),
                buffer -> State.values()[buffer.readInt()]
        );
    }
}
