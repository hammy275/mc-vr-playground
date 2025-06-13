package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.common.entity.EnergyBallEntity;
import com.hammy275.mcvrplayground.common.packet.UpdateEnergyBallPacketC2S;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRPoseHistory;

import java.util.List;

public class EnergyBallItem extends Item {

    private static final double GROW_SPEED_THRESHOLD = 0.05;

    public EnergyBallItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        // Stop item usage if player switches out of VR, or we somehow have a non-player using the item.
        if (livingEntity instanceof Player player && !VRAPI.instance().isVRPlayer(player) || !(livingEntity instanceof Player)) {
            livingEntity.stopUsingItem();
            return;
        }
        Player player = (Player) livingEntity;
        if (player.level().isClientSide) { // Historical VR data is only available on the client.
            // Get the historical VR data
            VRPoseHistory poseHistory = VRClientAPI.instance().getHistoricalVRPoses();

            // Get the average position over the past second of both the main-hand and the off-hand.
            Vec3 mainHandAveragePos = poseHistory.averagePosition(VRBodyPart.MAIN_HAND, 20);
            Vec3 offHandAveragePos = poseHistory.averagePosition(VRBodyPart.OFF_HAND, 20);

            // Average the averages to find the average center between the two hands.
            // This will be the new position of the ball.
            Vec3 handsCenterPos = mainHandAveragePos.add(offHandAveragePos).scale(0.5);

            // Get the average speed of both the main-hand and the off-hand.
            double mainHandSpeed = poseHistory.averageSpeed(VRBodyPart.MAIN_HAND, 5);
            double offHandSpeed = poseHistory.averageSpeed(VRBodyPart.OFF_HAND, 5);
            // If both the main-hand and off-hand are moving faster than the threshold, grow the ball as well.
            UpdateEnergyBallPacketC2S.State energyBallUpdateState = mainHandSpeed > GROW_SPEED_THRESHOLD && offHandSpeed > GROW_SPEED_THRESHOLD ?
                    UpdateEnergyBallPacketC2S.State.GROW : UpdateEnergyBallPacketC2S.State.NO_GROW;
            // Let the server know of the changed ball position and whether to grow it.
            NetworkManager.sendToServer(new UpdateEnergyBallPacketC2S(handsCenterPos, energyBallUpdateState));
            // Also move the ball on our end.
            EnergyBallEntity.getNearbyBall(player).ifPresent(ball -> {
                if (!ball.energyBallShot()) {
                    ball.setPos(handsCenterPos);
                }
            });
        }
    }

    @Override
    public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player && VRAPI.instance().isVRPlayer(player) && level.isClientSide) {
            VRPoseHistory poseHistory = VRClientAPI.instance().getHistoricalVRPoses();

            // Get the average velocity of each hand over the past few ticks.
            Vec3 mainHandVelocity = poseHistory.averageVelocity(VRBodyPart.MAIN_HAND, 5);
            Vec3 offHandVelocity = poseHistory.averageVelocity(VRBodyPart.OFF_HAND, 5);
            // Combine them to create one average velocity among both. This should give us both the direction to move
            // the ball and the speed at which to move it.
            Vec3 handsVelocity = mainHandVelocity.add(offHandVelocity).scale(0.5);
            // Tell the server to shoot the energy ball with the supplied velocity
            NetworkManager.sendToServer(new UpdateEnergyBallPacketC2S(handsVelocity, UpdateEnergyBallPacketC2S.State.SHOOT));
        }
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (VRAPI.instance().isVRPlayer(player)) {
            // Only let VR players use this item.
            player.startUsingItem(interactionHand);
            return InteractionResult.CONSUME;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
        for (int i = 1; i <= 3; i++) {
            list.add(Component.translatable("item.mc_vr_playground.energy_ball.desc." + i));
        }
    }
}
