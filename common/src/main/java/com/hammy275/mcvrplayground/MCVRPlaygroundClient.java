package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.client.render.ScaledItemRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class MCVRPlaygroundClient {

    public static void init() {
        EntityRendererRegistry.register(ModEntities.magicMissile, ScaledItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.energyBall, ScaledItemRenderer::new);
    }
}
