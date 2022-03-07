package net.blf02.vrplayground.common.network.packet;

import net.blf02.vrapi.api.data.IVRPlayer;
import net.blf02.vrplayground.common.init.ItemInit;
import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.util.ShootLaser;
import net.blf02.vrplayground.common.vr.VRPlugin;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class EmptyRightClickPacket {

    public static void encode(EmptyRightClickPacket packet, PacketBuffer buffer) {}

    public static EmptyRightClickPacket decode(PacketBuffer buffer) {
        return new EmptyRightClickPacket();
    }

    public static void handle(final EmptyRightClickPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayerEntity sender = ctx.get().getSender();
            if (sender != null) {
                // Client to server
                if (sender.inventory.armor.get(3).getItem() == ItemInit.laserHelmet.get()) {
                    IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(sender);

                    Vector3d pos = vrPlayer.getHMD().position();
                    Vector3d look = vrPlayer.getHMD().getLookAngle();

                    ShootLaser.shootLaser(sender.level, pos, look, sender);

                    CompoundNBT data = new CompoundNBT();
                    data.putDouble("lookX", look.x);
                    data.putDouble("lookY", look.y);
                    data.putDouble("lookZ", look.z);

                    data.putDouble("posX", pos.x);
                    data.putDouble("posY", pos.x);
                    data.putDouble("posZ", pos.x);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
