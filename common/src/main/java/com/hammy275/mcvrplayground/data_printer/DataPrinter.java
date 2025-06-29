package com.hammy275.mcvrplayground.data_printer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VRClientAPI;

public class DataPrinter extends Item {
    public DataPrinter(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (player.level().isClientSide) {
            if (VRClientAPI.instance().isVRActive()) {
                // "Print" all VRData retrieving functions from VRClientAPI.
                // Note that VRData and the VRPoses they contain all have valid toString() implementations to give useful data.
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.data_printer.begin"), false);
                player.displayClientMessage(Component.literal("getLatestRoomPose()").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal(VRClientAPI.instance().getLatestRoomPose().toString()), false);
                player.displayClientMessage(Component.literal("getPostTickRoomData()").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal(VRClientAPI.instance().getPostTickRoomPose().toString()), false);
                player.displayClientMessage(Component.literal("getPreTickWorldData()").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal(VRClientAPI.instance().getPreTickWorldPose().toString()), false);
                player.displayClientMessage(Component.literal("getPostTickWorldData()").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal(VRClientAPI.instance().getPostTickWorldPose().toString()), false);
                player.displayClientMessage(Component.literal("getWorldRenderData()").withStyle(ChatFormatting.UNDERLINE), false);
                player.displayClientMessage(Component.literal(VRClientAPI.instance().getWorldRenderPose().toString()), false);
                player.displayClientMessage(Component.translatable("item.mc_vr_playground.data_printer.end"), false);
            } else {
                player.displayClientMessage(Component.translatable("message.mc_vr_playground.not_in_vr"), false);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
