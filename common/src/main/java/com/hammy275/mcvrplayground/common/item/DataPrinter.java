package com.hammy275.mcvrplayground.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VRClientAPI;

public class DataPrinter extends Item {
    public DataPrinter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (player.level().isClientSide) {
            if (VRClientAPI.getInstance().isVRActive()) {
                // "Print" all VRData retrieving functions from VRClientAPI.
                // Note that VRData and the VRPoses they contain all have valid toString() implementations to give useful data.
                player.sendSystemMessage(Component.translatable("item.mc_vr_playground.data_printer.begin"));
                player.sendSystemMessage(Component.literal("getLatestRoomPose()").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal(VRClientAPI.getInstance().getLatestRoomPose().toString()));
                player.sendSystemMessage(Component.literal("getPostTickRoomData()").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal(VRClientAPI.getInstance().getPostTickRoomPose().toString()));
                player.sendSystemMessage(Component.literal("getPreTickWorldData()").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal(VRClientAPI.getInstance().getPreTickWorldPose().toString()));
                player.sendSystemMessage(Component.literal("getPostTickWorldData()").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal(VRClientAPI.getInstance().getPostTickWorldPose().toString()));
                player.sendSystemMessage(Component.literal("getWorldRenderData()").withStyle(ChatFormatting.UNDERLINE));
                player.sendSystemMessage(Component.literal(VRClientAPI.getInstance().getWorldRenderPose().toString()));
                player.sendSystemMessage(Component.translatable("item.mc_vr_playground.data_printer.end"));
            } else {
                player.sendSystemMessage(Component.translatable("message.mc_vr_playground.not_in_vr"));
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));
    }
}
