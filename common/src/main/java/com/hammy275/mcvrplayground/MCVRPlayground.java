package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.common.item.ModItems;
import com.hammy275.mcvrplayground.common.item.component.ModComponents;
import com.hammy275.mcvrplayground.common.packet.UpdateEnergyBallPacketC2S;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkManager;
import org.vivecraft.api.VRAPI;

public class MCVRPlayground {

    public static final String MOD_ID = "mc_vr_playground";

    public static void init() {
        ModComponents.COMPONENTS.register();
        ModItems.TABS.register();
        ModItems.ITEMS.register();
        ModEntities.ENTITIES.register();

        // Request 3 ticks of history (only what's needed on the server) on only the server.
        LifecycleEvent.SERVER_STARTED.register(ignored -> VRAPI.instance().requestTicksOfHistory(3));

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UpdateEnergyBallPacketC2S.TYPE,
                UpdateEnergyBallPacketC2S.STREAM_CODEC, UpdateEnergyBallPacketC2S::handle);
    }
}
