package com.hammy275.mcvrplayground.rainbow_trail;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.FBTMode;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.api.data.VRPoseHistory;

import java.util.List;

/**
 * Item that shows a VR player's pose from 3 ticks ago. This is intentionally run on the server to show that
 * server-side VR pose history works.
 */
public class RainbowTrail extends Item {

    private static final int[] colors = new int[]{0xFF0000, 0xFF7F00, 0xFFFF00, 0x00FF00, 0x0000FF, 0x7F00FF};

    public RainbowTrail(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
        // Note that the instanceof ServerPlayer check ensures this ony runs on the server, as this item primarily
        // ensures that functionality works.
        if (entity instanceof ServerPlayer player && VRAPI.instance().isVRPlayer(player)) {
            VRPoseHistory history = VRAPI.instance().getHistoricalVRPoses(player);
            // A player in VR is only guaranteed to have history if they are the local player. Since this is run on the
            // server, a player in VR may not have a history created yet. As such, a null check must be performed.
            if (history != null) {
                VRPose pose = history.getHistoricalData(3);
                if (pose != null) {
                    FBTMode fbtMode = pose.getFBTMode();
                    for (VRBodyPart bodyPart : VRBodyPart.values()) {
                        if (fbtMode.bodyPartAvailable(bodyPart)) {
                            // A color is chosen both by the player's tick count and the body part, such that each body
                            // part gets a different color each tick (barring how small the colors array is)
                            int colorIndex = (bodyPart.ordinal() + player.tickCount) % colors.length;
                            Vec3 pos = pose.getBodyPartData(bodyPart).getPos();
                            player.serverLevel().sendParticles(new DustParticleOptions(colors[colorIndex], 1f),
                                    pos.x, pos.y, pos.z, 1, 0.01, 0.01, 0.01, 0.0001);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.mc_vr_playground.rainbow_trail.desc.1"));
        list.add(Component.translatable("item.mc_vr_playground.rainbow_trail.desc.2"));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }
}
