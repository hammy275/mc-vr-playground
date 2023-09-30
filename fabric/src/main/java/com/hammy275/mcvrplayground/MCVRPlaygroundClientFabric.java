package com.hammy275.mcvrplayground;

import net.fabricmc.api.ClientModInitializer;

public class MCVRPlaygroundClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MCVRPlaygroundClient.init();
    }
}
