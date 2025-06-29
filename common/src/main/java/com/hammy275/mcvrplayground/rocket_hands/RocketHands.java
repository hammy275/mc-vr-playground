package com.hammy275.mcvrplayground.rocket_hands;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;
import org.vivecraft.api.data.VRPose;

public class RocketHands extends Item {

    private static final double ROCKET_MULT = -1d/16d; // Multiplier to scale the hand point direction by.

    public RocketHands(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (VRAPI.instance().isVRPlayer(player)) {
            player.startUsingItem(interactionHand);
        } else {
            // Fail if the player is NOT in VR and alert them that they're not in VR.
            if (player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.mc_vr_playground.not_in_vr"), false);
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksLeft) {
        super.onUseTick(level, livingEntity, itemStack, ticksLeft);

        // Check that we have a player and that the player is in VR
        if (livingEntity instanceof Player player && VRAPI.instance().isVRPlayer(player)) {
            // Get VR-related data for the player
            VRPose vrData = VRAPI.instance().getVRPose(player);

            // Get the direction both their controllers are pointing
            Vec3 mainHandDir = vrData.getMainHand().getDir();
            Vec3 offHandDir = vrData.getOffHand().getDir();

            // Decrease the magnitude of and flip the direction of where the controllers are pointing
            Vec3 c0DeltaMovementAdd = mainHandDir.scale(ROCKET_MULT);
            Vec3 c1DeltaMovementAdd = offHandDir.scale(ROCKET_MULT);

            // Add the modified controller direction as velocity to the player
            player.setDeltaMovement(player.getDeltaMovement().add(c0DeltaMovementAdd).add(c1DeltaMovementAdd));

            // Cancel fall damage if moving up
            if (player.getDeltaMovement().y() > 0) {
                player.resetFallDistance();
            }

            if (player.level().isClientSide()) {
                for (InteractionHand hand : InteractionHand.values()) { // Iterate over both controllers
                    // Rumble the controller
                    VRClientAPI.instance().triggerHapticPulse(VRBodyPart.fromInteractionHand(hand), 0.05f);

                    // Show particles coming out of the controller
                    Vec3 handPos = vrData.getHand(hand).getPos();
                    Vec3 handDir = vrData.getHand(hand).getDir();

                    for (int j = 0; j < 4; j++) { // Add 4 smoke particles
                        player.level().addParticle(
                            ParticleTypes.SMOKE,
                            handPos.x(), handPos.y(), handPos.z(),
                            // Using the controller rotation below as the spread of the particle works well
                            handDir.x(), handDir.y(), handDir.z()
                        );
                    }

                    player.level().addParticle( // Add 1 flame particle
                        ParticleTypes.FLAME,
                        handPos.x(), handPos.y(), handPos.z(),
                        // Using the controller rotation below as the spread of the particle works well
                        handDir.x(), handDir.y(), handDir.z()
                    );

                }
            }
        }

    }

    @Override
    public int getUseDuration(ItemStack itemStack, LivingEntity livingEntity) {
        return 72000;
    }
}
