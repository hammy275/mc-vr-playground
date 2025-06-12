package com.hammy275.mcvrplayground.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.client.VRRenderingAPI;
import org.vivecraft.api.data.VRPose;

public class DebugInfoItem extends Item {
    public DebugInfoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (player.level().isClientSide) { // Most data is always available on the client, even if the user isn't in VR
            player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.client_title").withStyle(ChatFormatting.UNDERLINE), true);
            player.displayClientMessage(Component.literal("isSeated(): " + VRClientAPI.instance().isSeated()), true);
            player.displayClientMessage(Component.literal("isLeftHanded(): " + VRClientAPI.instance().isLeftHanded()), true);
            player.displayClientMessage(Component.literal("getFBTMode(): " + VRClientAPI.instance().getFBTMode()), true);
            player.displayClientMessage(Component.literal("isVRInitialized(): " + VRClientAPI.instance().isVRInitialized()), true);
            player.displayClientMessage(Component.literal("isVRActive(): " + VRClientAPI.instance().isVRActive()), true);
            player.displayClientMessage(Component.literal("getWorldScale(): " + VRClientAPI.instance().getWorldScale()), true);
            player.displayClientMessage(Component.literal("isVanillaRenderPass(): " + VRRenderingAPI.instance().isVanillaRenderPass()), true);
            player.displayClientMessage(Component.literal("getCurrentRenderPass(): " + VRRenderingAPI.instance().getCurrentRenderPass()), true);
            player.displayClientMessage(Component.literal("isFirstRenderPass(): " + VRRenderingAPI.instance().isFirstRenderPass()), true);
            player.displayClientMessage(Component.literal("getHandRenderPos(MAIN_HAND): " + VRRenderingAPI.instance().getHandRenderPos(InteractionHand.MAIN_HAND)), true);
            player.displayClientMessage(Component.literal("getHandRenderPos(OFF_HAND): " + VRRenderingAPI.instance().getHandRenderPos(InteractionHand.OFF_HAND)), true);
        } else {
            VRPose pose = VRAPI.instance().getVRPose(player);
            if (pose != null) { // If it's null, we weren't in VR according to the server.
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.server_title").withStyle(ChatFormatting.UNDERLINE), true);
                player.displayClientMessage(Component.literal("isSeated(): " + pose.isSeated()), true);
                player.displayClientMessage(Component.literal("isLeftHanded(): " + pose.isLeftHanded()), true);
                player.displayClientMessage(Component.literal("getFBTMode(): " + pose.getFBTMode()), true);
            } else {
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.not_in_vr"), true);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
