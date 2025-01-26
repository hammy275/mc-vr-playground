package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.client.tracker.ShieldLookTracker;
import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.client.render.ScaledItemRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import org.vivecraft.api.client.VRClientAPI;

public class MCVRPlaygroundClient {

    public static final ShieldLookTracker shieldLookTracker = new ShieldLookTracker();

    public static void init() {
        VRClientAPI.instance().registerTracker(shieldLookTracker);
        VRClientAPI.instance().setTicksOfHistory(20); // This mod only needs up to 20 ticks of history

        EntityRendererRegistry.register(ModEntities.magicMissile, ScaledItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.energyBall, ScaledItemRenderer::new);
    }
}
