package com.hammy275.mcvrplayground.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.vivecraft.api.VivecraftAPI;
import org.vivecraft.api.client.VivecraftClientAPI;
import org.vivecraft.api.client.VivecraftRenderingAPI;
import org.vivecraft.api.data.VRPose;

public class DebugInfoItem extends Item {
    public DebugInfoItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (player.level().isClientSide) { // Most data is always available on the client, even if the user isn't in VR
            player.sendSystemMessage(Component.translatable("item.mc_vr_playground.debug_info.client_title").withStyle(ChatFormatting.UNDERLINE));
            player.sendSystemMessage(Component.literal("isSeated(): " + VivecraftClientAPI.getInstance().isSeated()));
            player.sendSystemMessage(Component.literal("isLeftHanded(): " + VivecraftClientAPI.getInstance().isLeftHanded()));
            player.sendSystemMessage(Component.literal("getFBTMode(): " + VivecraftClientAPI.getInstance().getFBTMode()));
            player.sendSystemMessage(Component.literal("isVRInitialized(): " + VivecraftClientAPI.getInstance().isVRInitialized()));
            player.sendSystemMessage(Component.literal("isVRActive(): " + VivecraftClientAPI.getInstance().isVRActive()));
            player.sendSystemMessage(Component.literal("getWorldScale(): " + VivecraftClientAPI.getInstance().getWorldScale()));
            player.sendSystemMessage(Component.literal("isVanillaRenderPass(): " + VivecraftRenderingAPI.getInstance().isVanillaRenderPass()));
            player.sendSystemMessage(Component.literal("getCurrentRenderPass(): " + VivecraftRenderingAPI.getInstance().getCurrentRenderPass()));
            player.sendSystemMessage(Component.literal("isFirstRenderPass(): " + VivecraftRenderingAPI.getInstance().isFirstRenderPass()));
        } else {
            VRPose pose = VivecraftAPI.getInstance().getVRPose(player);
            if (pose != null) { // If it's null, we weren't in VR according to the server.
                player.sendSystemMessage(Component.translatable("item.mc_vr_playground.debug_info.server_title").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal("isSeated(): " + pose.isSeated()));
                player.sendSystemMessage(Component.literal("isLeftHanded(): " + pose.isLeftHanded()));
                player.sendSystemMessage(Component.literal("getFBTMode(): " + pose.getFBTMode()));
            } else {
                player.sendSystemMessage(Component.translatable("item.mc_vr_playground.debug_info.not_in_vr"));
            }
        }

        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }
}
