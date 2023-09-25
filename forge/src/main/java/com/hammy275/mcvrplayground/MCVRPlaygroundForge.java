package com.hammy275.mcvrplayground;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MCVRPlayground.MOD_ID)
public class MCVRPlaygroundForge {
    public MCVRPlaygroundForge() {
        EventBuses.registerModEventBus(MCVRPlayground.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
