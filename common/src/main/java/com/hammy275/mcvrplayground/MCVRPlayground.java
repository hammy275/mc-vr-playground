package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.shared.ModEntities;
import com.hammy275.mcvrplayground.shared.ModItems;
import com.hammy275.mcvrplayground.shared.ModComponents;
import com.hammy275.mcvrplayground.energy_ball.UpdateEnergyBallPacketC2S;
import dev.architectury.networking.NetworkManager;

public class MCVRPlayground {

    public static final String MOD_ID = "mc_vr_playground";

    public static void init() {
        ModComponents.COMPONENTS.register();
        ModItems.TABS.register();
        ModItems.ITEMS.register();
        ModEntities.ENTITIES.register();

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UpdateEnergyBallPacketC2S.TYPE,
                UpdateEnergyBallPacketC2S.STREAM_CODEC, UpdateEnergyBallPacketC2S::handle);
    }
}
