package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.MCVRPlayground;
import com.hammy275.mcvrplayground.common.entity.EnergyBallEntity;
import com.hammy275.mcvrplayground.common.packet.UpdateEnergyBallPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.client.data.VRPoseHistory;
import org.vivecraft.api.data.VRBodyPart;

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
            UpdateEnergyBallPacket.Type energyBallUpdateType = mainHandSpeed > GROW_SPEED_THRESHOLD && offHandSpeed > GROW_SPEED_THRESHOLD ?
                    UpdateEnergyBallPacket.Type.GROW : UpdateEnergyBallPacket.Type.NO_GROW;
            // Let the server know of the changed ball position and whether to grow it.
            MCVRPlayground.NETWORK.sendToServer(new UpdateEnergyBallPacket(handsCenterPos, energyBallUpdateType));
            // Also move the ball on our end.
            EnergyBallEntity.getNearbyBall(player).ifPresent(ball -> {
                if (!ball.energyBallShot()) {
                    ball.setPos(handsCenterPos);
                }
            });
        }
    }

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i) {
        if (livingEntity instanceof Player player && VRAPI.instance().isVRPlayer(player) && level.isClientSide) {
            VRPoseHistory poseHistory = VRClientAPI.instance().getHistoricalVRPoses();

            // Get the average velocity of each hand over the past few ticks.
            Vec3 mainHandVelocity = poseHistory.averageVelocity(VRBodyPart.MAIN_HAND, 5);
            Vec3 offHandVelocity = poseHistory.averageVelocity(VRBodyPart.OFF_HAND, 5);
            // Combine them to create one average velocity among both. This should give us both the direction to move
            // the ball and the speed at which to move it.
            Vec3 handsVelocity = mainHandVelocity.add(offHandVelocity).scale(0.5);
            // Tell the server to shoot the energy ball with the supplied velocity
            MCVRPlayground.NETWORK.sendToServer(new UpdateEnergyBallPacket(handsVelocity, UpdateEnergyBallPacket.Type.SHOOT));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (VRAPI.instance().isVRPlayer(player)) {
            // Only let VR players use this item.
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(itemStack);
        } else {
            return InteractionResultHolder.pass(itemStack);
        }
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000; // Vanilla uses 1 hour for items that can be used forever.
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.NONE; // No use animation, the creation of the energy ball entity is the signaling of usage.
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        for (int i = 1; i <= 3; i++) {
            list.add(Component.translatable("item.mc_vr_playground.energy_ball.desc." + i));
        }
    }
}
