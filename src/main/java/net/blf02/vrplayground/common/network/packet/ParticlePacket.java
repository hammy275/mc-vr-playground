package net.blf02.vrplayground.common.network.packet;

import net.blf02.vrplayground.common.util.ShootLaser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ParticlePacket {

    public static final int LASER_SHOOT = 1;

    protected final int action;
    protected final CompoundNBT data;

    public ParticlePacket(int action, CompoundNBT data) {
        this.action = action;
        this.data = data;
    }

    public static void encode(ParticlePacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.action);
        buffer.writeNbt(packet.data);
    }

    public static ParticlePacket decode(PacketBuffer buffer) {
        return new ParticlePacket(buffer.readInt(), buffer.readNbt());
    }

    public static void handle(final ParticlePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender == null) {
                // Server to client
                clientHandle(message.action, message.data);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static void clientHandle(int action, CompoundNBT data) {
        System.out.println("Action: " + action);
        if (action == ParticlePacket.LASER_SHOOT) {
            Vector3d lookVec = new Vector3d(data.getDouble("lookX"), data.getDouble("lookY"),
                    data.getDouble("lookZ"));
            Vector3d posVec = new Vector3d(data.getDouble("posX"), data.getDouble("posY"),
                    data.getDouble("posZ"));
            ShootLaser.shootLaser(Minecraft.getInstance().level, posVec, lookVec, null);
        }
    }

}
