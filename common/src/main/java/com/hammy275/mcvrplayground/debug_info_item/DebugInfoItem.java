package com.hammy275.mcvrplayground.debug_info_item;

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
            player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.client_title").withStyle(ChatFormatting.UNDERLINE), false);
            player.displayClientMessage(Component.literal("isSeated(): " + VRClientAPI.instance().isSeated()), false);
            player.displayClientMessage(Component.literal("isLeftHanded(): " + VRClientAPI.instance().isLeftHanded()), false);
            player.displayClientMessage(Component.literal("getFBTMode(): " + VRClientAPI.instance().getFBTMode()), false);
            player.displayClientMessage(Component.literal("isVRInitialized(): " + VRClientAPI.instance().isVRInitialized()), false);
            player.displayClientMessage(Component.literal("isVRActive(): " + VRClientAPI.instance().isVRActive()), false);
            player.displayClientMessage(Component.literal("getWorldScale(): " + VRClientAPI.instance().getWorldScale()), false);
            player.displayClientMessage(Component.literal("isVanillaRenderPass(): " + VRRenderingAPI.instance().isVanillaRenderPass()), false);
            player.displayClientMessage(Component.literal("getCurrentRenderPass(): " + VRRenderingAPI.instance().getCurrentRenderPass()), false);
            player.displayClientMessage(Component.literal("isFirstRenderPass(): " + VRRenderingAPI.instance().isFirstRenderPass()), false);
            player.displayClientMessage(Component.literal("getHandRenderPos(MAIN_HAND): " + VRRenderingAPI.instance().getHandRenderPos(InteractionHand.MAIN_HAND)), false);
            player.displayClientMessage(Component.literal("getHandRenderPos(OFF_HAND): " + VRRenderingAPI.instance().getHandRenderPos(InteractionHand.OFF_HAND)), false);
        } else {
            VRPose pose = VRAPI.instance().getVRPose(player);
            if (pose != null) { // If it's null, we weren't in VR according to the server.
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.server_title").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal("isSeated(): " + pose.isSeated()), false);
                player.displayClientMessage(Component.literal("isLeftHanded(): " + pose.isLeftHanded()), false);
                player.displayClientMessage(Component.literal("getFBTMode(): " + pose.getFBTMode()), false);
            } else {
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.debug_info.not_in_vr"), false);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
