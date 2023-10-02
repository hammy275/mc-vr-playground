package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.client.render.MagicMissileEntityRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;

public class MCVRPlaygroundClient {

    public static void init() {
        EntityRendererRegistry.register(ModEntities.magicMissile, MagicMissileEntityRenderer::new);
    }
}
