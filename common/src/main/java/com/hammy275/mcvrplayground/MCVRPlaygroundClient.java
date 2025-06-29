package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.shield_look.ShieldLookTracker;
import com.hammy275.mcvrplayground.shared.ModEntities;
import com.hammy275.mcvrplayground.shared.ScaledItemRenderer;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import org.vivecraft.api.client.VRClientAPI;

public class MCVRPlaygroundClient {

    public static final ShieldLookTracker shieldLookTracker = new ShieldLookTracker();

    public static void init() {
        // Registers the ShieldLookTracker with Vivecraft.
        VRClientAPI.instance().addClientRegistrationHandler(event -> event.registerTrackers(shieldLookTracker));

        EntityRendererRegistry.register(ModEntities.magicMissile, ScaledItemRenderer::new);
        EntityRendererRegistry.register(ModEntities.energyBall, ScaledItemRenderer::new);
    }
}
