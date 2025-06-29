package com.hammy275.mcvrplayground.other_player_vr_visualizer;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.FBTMode;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRPose;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class OtherVRPlayerVisualizer extends Item {

    // Usually, one would want to store this in the NBT/data components of the item. However, since
    // we're testing client functionality here, I'm just storing this in a map on the client. This
    // does lead to several bugs (data saved between worlds, all item stacks "holding" the same
    // data, etc.) but this is meant more as a demo than anything else.
    private static final Map<VRBodyPart, Vec3> bodyPartPositions = new EnumMap<>(VRBodyPart.class);

    public OtherVRPlayerVisualizer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        // Item is meant to test getting other VR players from the client, so we run client-only
        if (player.level().isClientSide) {
            // Gets nearby players that aren't us that are in VR.
            List<Entity> nearbyPlayers = level.getEntities(player, AABB.ofSize(player.position(), 16, 16, 16),
                    e -> e instanceof Player p && VRAPI.instance().isVRPlayer(p));
            // Display a message and return if no such players exist.
            if (nearbyPlayers.isEmpty()) {
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.other_vr_player_visualizer.fail"), false);
                return InteractionResult.FAIL;
            }
            // Get the first player found in the list. May not necessarily be the nearest, but that's okay.
            Player target = (Player) nearbyPlayers.getFirst();
            // Get the pose of all body parts of the player.
            VRPose pose = VRAPI.instance().getVRPose(target);
            // pose is not null since the player was checked to be in VR, so this is safe.
            FBTMode fbtMode = pose.getFBTMode();
            // Clear the map we store positions in.
            bodyPartPositions.clear();
            // For each possible body part
            for (VRBodyPart vrBodyPart : VRBodyPart.values()) {
                // Check if that body part is available in the FBT mode the player is in.
                if (fbtMode.bodyPartAvailable(vrBodyPart)) {
                    // If it is, add it to our map of body parts to positions.
                    // Since the body part is available, the body part data is guaranteed to not be null.
                    bodyPartPositions.put(vrBodyPart, pose.getBodyPartData(vrBodyPart).getPos());
                }
            }
            // We successfully captured data from a VR user.
            player.displayClientMessage(Component.translatable("item.mc_vr_playground.other_vr_player_visualizer.success", target.getScoreboardName()), false);
            return InteractionResult.SUCCESS;
        } else {
            // Just pass on item use on the server.
            return InteractionResult.PASS;
        }
    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);
        // For each body part and position we have stored
        for (Map.Entry<VRBodyPart, Vec3> entry : bodyPartPositions.entrySet()) {
            // Get a color for visualizing the body part for the player.
            int color = switch (entry.getKey()) {
                case HEAD -> 0xFFFFFF; // White for HMD
                case MAIN_HAND -> 0x0000FF; // Blue for main-hand
                case OFF_HAND -> 0xFF0000; // Red for off-hand
                default -> 0x7F7F7F; // Gray for other body parts
            };
            // Get the stored position.
            Vec3 pos = entry.getValue();
            // Add a dust particle at the position with the color.
            level.addParticle(new DustParticleOptions(color, 1f),
                    pos.x, pos.y, pos.z,
                    0, 0, 0);
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        list.add(Component.translatable("item.mc_vr_playground.other_vr_player_visualizer.desc"));
    }
}
