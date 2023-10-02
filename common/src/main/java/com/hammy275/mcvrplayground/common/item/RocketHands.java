package com.hammy275.mcvrplayground.common.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VivecraftAPI;
import org.vivecraft.api.client.VivecraftClientAPI;
import org.vivecraft.api.data.VRData;

public class RocketHands extends Item {

    private static final double ROCKET_MULT = -1d/16d; // Multiplier to scale the hand point direction by.

    public RocketHands(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (VivecraftAPI.getInstance().isVRPlayer(player)) {
            player.startUsingItem(interactionHand);
        } else {
            // Fail if the player is NOT in VR and alert them that they're not in VR.
            if (player.level().isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.mc_vr_playground.not_in_vr"));
            }
        }
        return InteractionResultHolder.consume(player.getItemInHand(interactionHand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksLeft) {
        super.onUseTick(level, livingEntity, itemStack, ticksLeft);

        // Check that we have a player and that the player is in VR
        if (livingEntity instanceof Player player && VivecraftAPI.getInstance().isVRPlayer(player)) {
            // Get VR-related data for the player
            VRData vrData = VivecraftAPI.getInstance().getVRData(player);

            // Get the direction both their controllers are pointing
            Vec3 c0Dir = vrData.getController0().getRot();
            Vec3 c1Dir = vrData.getController1().getRot();

            // Decrease the magnitude of and flip the direction of where the controllers are pointing
            Vec3 c0DeltaMovementAdd = c0Dir.scale(ROCKET_MULT);
            Vec3 c1DeltaMovementAdd = c1Dir.scale(ROCKET_MULT);

            // Add the modified controller direction as velocity to the player
            player.setDeltaMovement(player.getDeltaMovement().add(c0DeltaMovementAdd).add(c1DeltaMovementAdd));

            // Cancel fall damage if moving up
            if (player.getDeltaMovement().y() > 0) {
                player.resetFallDistance();
            }

            if (player.level().isClientSide()) {
                for (int controllerNum = 0; controllerNum <= 1; controllerNum++) { // Iterate over both controllers
                    // Rumble the controller
                    VivecraftClientAPI.getInstance().triggerHapticPulse(controllerNum, 0.05f);

                    // Show particles coming out of the controller
                    Vec3 controllerPos = vrData.getController(controllerNum).getPos();
                    Vec3 controllerDir = vrData.getController(controllerNum).getRot();

                    for (int j = 0; j < 4; j++) { // Add 4 smoke particles
                        player.level().addParticle(
                            ParticleTypes.SMOKE,
                            controllerPos.x(), controllerPos.y(), controllerPos.z(),
                            // Using the controller rotation below as the spread of the particle works well
                            controllerDir.x(), controllerDir.y(), controllerDir.z()
                        );
                    }

                    player.level().addParticle( // Add 1 flame particle
                        ParticleTypes.FLAME,
                        controllerPos.x(), controllerPos.y(), controllerPos.z(),
                        // Using the controller rotation below as the spread of the particle works well
                        controllerDir.x(), controllerDir.y(), controllerDir.z()
                    );

                }
            }
        }

    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }
}
