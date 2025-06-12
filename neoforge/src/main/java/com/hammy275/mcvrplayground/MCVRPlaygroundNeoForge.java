package com.hammy275.mcvrplayground;

import dev.architectury.platform.Platform;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(MCVRPlayground.MOD_ID)
public class MCVRPlaygroundNeoForge {
    public MCVRPlaygroundNeoForge() {
        MCVRPlayground.init();
        if (Platform.getEnv() == Dist.CLIENT) {
            MCVRPlaygroundClient.init();
        }
    }
}
