package com.hammy275.mcvrplayground.common.packet;

import com.hammy275.mcvrplayground.common.entity.EnergyBallEntity;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;

import java.util.Optional;
import java.util.function.Supplier;

public record UpdateEnergyBallPacket(Vec3 vec, Type type) {

    public UpdateEnergyBallPacket(FriendlyByteBuf buffer) {
        this(buffer.readVec3(), buffer.readEnum(Type.class));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVec3(this.vec);
        buffer.writeEnum(this.type);
    }

    public void handle(Supplier<NetworkManager.PacketContext> context) {
        context.get().queue(() -> {
            Player ctxPlayer = context.get().getPlayer();
            // Client sent us a packet to update or create an energy ball and they're in VR.
            if (ctxPlayer instanceof ServerPlayer player && VRAPI.instance().isVRPlayer(player)) {
                // Check that the new ball position is within a reasonable distance of the player.
                if (vec.distanceToSqr(player.getEyePosition()) > 3 * 3 && this.type != Type.SHOOT) {
                    return;
                }
                // Find a nearby ball that this player is the owner of
                Optional<EnergyBallEntity> ballOpt = EnergyBallEntity.getNearbyBall(player);

                if (ballOpt.isEmpty()) {
                    // No ball found, make a new one!
                    if (this.type != Type.SHOOT) {
                        EnergyBallEntity.createFromVRPlayer(player, vec);
                    }
                } else {
                    // Ball found. Update it if not shot, or do nothing if it has been.
                    EnergyBallEntity ball = ballOpt.get();
                    if (!ball.energyBallShot()) {
                        if (this.type == Type.SHOOT) {
                            ball.shoot(vec);
                        } else {
                            ball.moveTo(vec);
                            if (this.type == Type.GROW) {
                                ball.grow();
                            }
                        }
                    }
                }
            }
        });
    }

    public enum Type {
        NO_GROW, GROW, SHOOT;
    }
}
