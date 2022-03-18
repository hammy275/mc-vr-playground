package net.blf02.vrplayground.common.data;

import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.api.data.IVRPlayer;
import net.blf02.vrplayground.common.vr.VRPlugin;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import javax.annotation.Nullable;
import java.util.List;

/**
 * NOTE: For those trying to understand the API, it's recommended to look at something far more simple.
 * If you want a more advanced kind of thing to look at once you have the basics down though, this is a good spot!
 */
public class ForceInformation {

    public final PlayerEntity player;
    public LivingEntity target0;
    public LivingEntity target1;
    public double target0Dist = -1;
    public double target1Dist = -1;

    public ForceInformation(PlayerEntity player, @Nullable LivingEntity target0, @Nullable LivingEntity target1) {
        this.player = player;
        this.target0 = getValidTarget(target0);
        this.target1 = target1 == target0 ? null : getValidTarget(target1); // Don't set target0 == target1
        if (target0 != null) this.target0Dist = player.distanceTo(target0);
        if (target1 != null) this.target1Dist = player.distanceTo(target1);
    }

    // Ensures that the initial targets are "good" (not bosses basically)
    protected LivingEntity getValidTarget(LivingEntity potentialTarget) {
        if (potentialTarget == null) {
            return null;
        } else if (potentialTarget.getMaxHealth() > 50) {
            return null;
        }

        return potentialTarget;
    }

    public boolean tick() {
        // Stop caring about a target if it's dead
        if (target0 != null && !target0.isAlive()) {
            target0 = null;
            target0Dist = -1;
        }
        if (target1 != null && !target1.isAlive()) {
            target1 = null;
            target1Dist = -1;
        }

        // Request a deletion if we don't have a target anymore
        if (target0 == null && target1 == null) {
            return true; // Return true if we don't need this anymore
        }

        // Process our held entities for both controllers
        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player);
        processVictim(target0, vrPlayer.getController0(), target0Dist);
        processVictim(target1, vrPlayer.getController1(), target1Dist);

        // If controller 0's target is null but the controller is hovering over 1's target
        // The else if does the same vice-versa
        if (target0 == null && target1 != null && traceToFirstMob(player, vrPlayer.getController0(), 50) == target1) {
            processBothHandsVictim(target1);
        } else if (target1 == null && target0 != null && traceToFirstMob(player, vrPlayer.getController1(), 50) == target0) {
            processBothHandsVictim(target0);
        }

        // Don't want to delete, so return false
        return false;
    }

    protected void processBothHandsVictim(LivingEntity victim) {
        // Deal 7-10 damage
        if (victim.tickCount % 20 == 0) {
            victim.hurt(DamageSource.playerAttack(player), victim.getRandom().nextInt(4) + 7);
        }
    }

    protected void processVictim(LivingEntity victim, IVRData controller, double dist) {
        if (victim == null) return; // Ignore if our victim is null

        // Get look angle of the controller
        Vector3d lookVec = controller.getLookAngle();

        // Get the distance for our look vector
        double oneDist = Math.sqrt(lookVec.x * lookVec.x + lookVec.y * lookVec.y + lookVec.z * lookVec.z);

        // Calculate some theta values
        double thetaXZ = Math.atan(lookVec.z / lookVec.x);
        if (lookVec.x < 0) {
            thetaXZ += Math.PI;
        }
        double thetaY = Math.acos(lookVec.y / oneDist);

        // Get the new x, y, and z offsets based on the distance and the current angle
        double newX = dist * Math.cos(thetaXZ) * Math.sin(thetaY);
        double newY = dist * Math.cos(thetaY);
        double newZ = dist * Math.sin(thetaXZ) * Math.sin(thetaY);

        // Set the new position accordingly, only if we aren't moving them into a block
        Vector3d newPos = controller.position().add(newX, newY, newZ);
        if (!victim.level.getBlockState(new BlockPos(newPos)).canOcclude()) {
            victim.teleportTo(newPos.x, newPos.y, newPos.z);
        }

        // Reset some things so the victim doesn't act buggy
        victim.fallDistance = 0;
        victim.setDeltaMovement(0, 0, 0);

        // If we're holding a hostile mob, we should remove their attack target to simulate not being
        // able to do anything
        if (victim instanceof MobEntity) {
            MobEntity hostile = (MobEntity) victim;
            hostile.setTarget(null);
        }
    }

    protected static LivingEntity traceToFirstMob(PlayerEntity player, IVRData data, int iters) {
        Vector3d pos = data.position();
        Vector3d look = data.getLookAngle(); // Grab the controller's position and look angle
        for (int i = 0; i < iters; i++) {
            // Move ahead 0.2 blocks
            pos = pos.add(look.x / 5f, look.y / 5f, look.z / 5f);

            // If we collide with a block, we bail
            BlockState hitBlock = player.level.getBlockState(new BlockPos(pos));
            if (hitBlock.canOcclude()) {
                break;
            }

            // Get all entities nearby that are living and aren't spectating players
            List<Entity> hits = player.level.getEntities(player, new AxisAlignedBB(pos.x - 0.1, pos.y - 0.1, pos.z - 0.1,
                            pos.x + 0.1, pos.y + 0.1, pos.z + 0.1),
                    (ent) -> !ent.isSpectator() && ent instanceof LivingEntity);
            // If we have an entity, set it as our result and break.
            if (hits.size() > 0) {
                return (LivingEntity) hits.get(0); // Predicate makes sure we always get a LivingEntity
            }
        }
        return null;
    }

    public static ForceInformation createForceInteraction(PlayerEntity player) {
        if (!VRPlugin.API.playerInVR(player)) {
            return null; // Return null if player not in VR
        }
        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player);
        LivingEntity target0 = null;
        LivingEntity target1 = null;
        for (int c = 0; c <= 1; c++) { // Iterate for each controller
            IVRData data = vrPlayer.getController(c);
            LivingEntity res = traceToFirstMob(player, data, 100); // Get the first mob ray traced from the controller
            // If we have a resulting entity, set it to the appropriate controller
            if (res != null) {
                if (c == 0) {
                    target0 = res;
                } else {
                    target1 = res;
                }
            }
        }
        // If we didn't find a target in both controllers, return null
        if (target0 == null && target1 == null) {
            return null;
        }
        // Return an instance if we have a target.
        return new ForceInformation(player, target0, target1);
    }

}
