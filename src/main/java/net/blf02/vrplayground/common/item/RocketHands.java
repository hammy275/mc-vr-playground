package net.blf02.vrplayground.common.item;

import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.api.data.IVRPlayer;
import net.blf02.vrplayground.common.vr.VRPlugin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class RocketHands extends Item {

    public static final double mult = -0.25;

    public RocketHands(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        // Return early if player isn't in VR.
        if (!VRPlugin.API.playerInVR(player)) return ActionResult.fail(player.getItemInHand(hand));

        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player); // Get VR Player instance

        // Get the direction each controller is pointing, flip it backwards, then shrink it
        Vector3d c0 = vrPlayer.getController0().getLookAngle().multiply(mult, mult, mult);
        Vector3d c1 = vrPlayer.getController1().getLookAngle().multiply(mult, mult, mult);

        // Add the flipped, shrunken controller direction to our movement
        player.setDeltaMovement(player.getDeltaMovement().add(c0).add(c1));

        // If we're currently moving up, cancel our fall damage
        if (player.getDeltaMovement().y >= 0) {
            player.fallDistance = 0; // Reset fall distance if we're going up
        }

        if (!level.isClientSide) {
            ServerWorld serverLevel = (ServerWorld) level;
            for (int i = 0; i <= 1; i++) {
                IVRData controller = vrPlayer.getController(i); // Get controller number i (0 and 1)
                Vector3d pos = controller.position(); // Get the position of the controller
                Vector3d look = controller.getLookAngle(); // Get the angle the controller is pointing
                serverLevel.sendParticles(ParticleTypes.SMOKE, // Add smoke particles
                        pos.x, pos.y, pos.z, // At the position of our controllers
                        4, // Adding 4 particles
                        look.x, look.y, look.z, // Spreading out in the direction of our controller
                        0.05 // Moving at the speed 0.05
                        );
                serverLevel.sendParticles(ParticleTypes.FLAME, // Add a flame particle
                        pos.x, pos.y, pos.z, // At the position of the controller
                        1, // Only one particle
                        look.x, look.y, look.z, // Spreading out in the direction of our controller
                        0); // Make the particle not move
            }
        } else {
            for (int i = 0; i <= 1; i++) { // For both controllers
                VRPlugin.API.triggerHapticPulse(i, 0.25f, null); // Rumble controller for 0.25 secs
            }
        }
        return ActionResult.success(player.getItemInHand(hand));
    }
}
