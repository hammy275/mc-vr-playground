package com.hammy275.mcvrplayground.client.proxy;

import com.hammy275.mcvrplayground.common.item.HistoryVisualizer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.vivecraft.api.client.VivecraftClientAPI;
import org.vivecraft.api.client.data.VRPoseHistory;
import org.vivecraft.api.data.VRPose;

public class HistoryVisualizerClientTick {

    public static void tick(ItemStack itemStack, Entity entity) {
        // Only run if the entity holding the item is us AND only run every 0.5 seconds.
        // Note that checking if we're on the client-side and that the player is in VR is already
        // done in HistoryVisualizer.java
        if (entity == Minecraft.getInstance().player && entity.tickCount % 10 == 0) {
            // Item stores which device (HMD or a controller) we're visualizing in its NBT data
            HistoryVisualizer.VisualizerMode mode = HistoryVisualizer.getMode(itemStack);

            VRPoseHistory history;
            if (mode == HistoryVisualizer.VisualizerMode.HMD) {
                // Get the history data for the HMD
                history = VivecraftClientAPI.getInstance().getHistoricalVRHMDPoses();
            } else {
                // Get the history data for either controller 0 or controller 1
                history = VivecraftClientAPI.getInstance().getHistoricalVRControllerPoses(mode.ordinal());
            }

            for (int i = 0; i < history.ticksOfHistory(); i++) {
                // This makes the particle larger the farther it is away from the player.
                // Remember that index 0 of history is the newest while the last index
                // (which is always history.ticksOfHistory() - 1) is the oldest known position.
                float particleSize = (i + 1) * (1f / VRPoseHistory.MAX_TICKS_BACK);
                VRPose entry = history.getHistoricalData(i); // Get the entry from our history
                Vec3 entryPos = entry.getPos(); // Get the position of said history entry
                entity.level().addParticle(
                        new DustParticleOptions(new Vector3f(1f, 0.5f, 0f), particleSize),
                        entryPos.x(), entryPos.y(), entryPos.z(),
                        0, 0, 0
                ); // Add a particle there
            }
        }
    }
}
