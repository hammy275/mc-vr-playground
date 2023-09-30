package com.hammy275.mcvrplayground;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MCVRPlayground.MOD_ID)
public class MCVRPlaygroundForge {
    public MCVRPlaygroundForge() {
        EventBuses.registerModEventBus(MCVRPlayground.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        MCVRPlayground.init();
        if (Platform.getEnv() == Dist.CLIENT) {
            MCVRPlaygroundClient.init();
        }
    }
}
