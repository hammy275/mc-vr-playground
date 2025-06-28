package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.client.tracker.ShieldLookTracker;
import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.client.render.ScaledItemRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import org.vivecraft.api.client.VRClientAPI;

public class MCVRPlaygroundClient {

    public static final ShieldLookTracker shieldLookTracker = new ShieldLookTracker();

    public static void init() {
        VRClientAPI.instance().addClientRegistrationHandler(event -> event.registerTrackers(shieldLookTracker));

        EntityRendererRegistry.register(ModEntities.magicMissile, ScaledItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.energyBall, ScaledItemRenderer::new);
    }
}
