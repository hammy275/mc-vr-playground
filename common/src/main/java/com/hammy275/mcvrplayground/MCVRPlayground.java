package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.common.item.ModItems;
import com.hammy275.mcvrplayground.common.packet.UpdateEnergyBallPacket;
import dev.architectury.networking.NetworkChannel;
import net.minecraft.resources.ResourceLocation;

public class MCVRPlayground {

    public static final String MOD_ID = "mc_vr_playground";

    public static final NetworkChannel NETWORK = NetworkChannel.create(new ResourceLocation(MOD_ID, "network"));

    public static void init() {
        ModItems.TABS.register();
        ModItems.ITEMS.register();
        ModEntities.ENTITIES.register();

        NETWORK.register(UpdateEnergyBallPacket.class, UpdateEnergyBallPacket::encode, UpdateEnergyBallPacket::new, UpdateEnergyBallPacket::handle);
    }
}
