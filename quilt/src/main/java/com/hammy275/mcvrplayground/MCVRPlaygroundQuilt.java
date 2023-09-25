package com.hammy275.mcvrplayground;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

public class MCVRPlaygroundQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        MCVRPlayground.init();
    }
}
