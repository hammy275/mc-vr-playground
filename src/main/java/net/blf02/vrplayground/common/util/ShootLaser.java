package net.blf02.vrplayground.common.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class ShootLaser {

    public static void shootLaser(World level, Vector3d pos, Vector3d lookVec, PlayerEntity shooter) {
        if (!(level instanceof ServerWorld)) return;
        for (int i = 0; i < 100; i++) {
            pos = pos.add(lookVec);
            ((ServerWorld) level).sendParticles(new RedstoneParticleData(1, 0, 0, 1),
                    pos.x, pos.y, pos.z, 1,0,0, 0, 0);
            AxisAlignedBB hit = new AxisAlignedBB(pos.add(-0.5, -0.5, -0.5),
                    pos.add(0.5, 0.5, 0.5));
            List<Entity> hits = level.getEntities(shooter, hit);
            for (Entity e : hits) {
                e.hurt(DamageSource.playerAttack(shooter), 7); // Hardcoded 7 damage
                e.setSecondsOnFire(4);
            }
        }
    }
}
