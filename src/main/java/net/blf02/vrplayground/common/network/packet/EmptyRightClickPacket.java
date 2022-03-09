package net.blf02.vrplayground.common.network.packet;

import net.blf02.vrapi.api.data.IVRData;
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

                    for (int i = 0; i <= 1; i++) { // Iterate for eyes 0 and 1 (left and right)
                        IVRData eye = vrPlayer.getEye(i);
                        ShootLaser.shootLaser(sender.level, eye.position(), eye.getLookAngle(), sender);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }


}
