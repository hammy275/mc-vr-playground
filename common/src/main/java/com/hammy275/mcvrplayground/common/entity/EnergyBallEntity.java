package com.hammy275.mcvrplayground.common.entity;

import com.hammy275.mcvrplayground.common.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class EnergyBallEntity extends Projectile implements ScaledItemSupplier {

    public static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(EnergyBallEntity.class,
            EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> SHOT = SynchedEntityData.defineId(EnergyBallEntity.class,
            EntityDataSerializers.BOOLEAN);
    private static final ItemStack ITEM = new ItemStack(ModItems.ENERGY_BALL.get());

    private int ticksShot = 0;

    public EnergyBallEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public static EnergyBallEntity createFromVRPlayer(Player owner, Vec3 centerPos) {
        EnergyBallEntity energyBall = new EnergyBallEntity(ModEntities.energyBall.get(), owner.level());
        energyBall.setOwner(owner);
        energyBall.setPos(centerPos);
        energyBall.entityData.set(SCALE, 0.5f);
        owner.level().addFreshEntity(energyBall);
        return energyBall;
    }

    public static Optional<EnergyBallEntity> getNearbyBall(Player player) {
        List<EnergyBallEntity> energyBalls = player.level().getEntitiesOfClass(EnergyBallEntity.class,
                AABB.ofSize(player.getEyePosition(), 10, 10, 10), ball -> ball.getOwner() == player);
        return energyBalls.isEmpty() ? Optional.empty() : Optional.of(energyBalls.get(0));
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.position().add(this.getDeltaMovement()));
        // Handle hit detection like a projectile
        if (this.energyBallShot()) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS && !this.isRemoved()) {
                this.onHit(hitResult);
            }
            // Handle hit detection for the entire hitbox as well
            if (!this.isRemoved()) {
                float scale = this.getScale();
                List<Entity> entitiesInBox = this.level().getEntities(this.getOwner(), AABB.ofSize(this.getEyePosition(), scale, scale, scale),
                        this::canHitEntity);
                if (!entitiesInBox.isEmpty()) {
                    onHit(new EntityHitResult(entitiesInBox.get(0)));
                }
            }
            // Discard if ball has been shot for 5 seconds, or it's moving very slowly
            if (++this.ticksShot > 100) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity victim = entityHitResult.getEntity();
        DamageSource damageSource;
        if (this.getOwner() instanceof Player player) {
            // Ideally, we'd use a custom damage source here so we could have a unique death message.
            // However, to keep things simple, we're using the playerAttack one here instead.
            damageSource = this.damageSources().playerAttack(player);
        } else {
            damageSource = this.damageSources().generic();
        }
        victim.hurt(damageSource, 5f * this.getScale()); // Deal damage to victim based on scale.
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SCALE, 0.5f);
        builder.define(SHOT, false);
    }

    public boolean energyBallShot() {
        return this.entityData.get(SHOT);
    }

    public void grow() {
        this.entityData.set(SCALE, Math.min(this.getScale() + 0.05f, 3f));
    }

    public void shoot(Vec3 shootVec) {
        // Scale shootVec so it isn't too slow
        shootVec = shootVec.scale(10);
        // Cap shootVec to not be faster than 1 block/t
        if (shootVec.lengthSqr() > 1) {
            shootVec = shootVec.normalize();
        }
        this.entityData.set(SHOT, true);
        this.setDeltaMovement(shootVec);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return ITEM;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public float getScale() {
        return this.getEntityData().get(SCALE);
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && entity != this.getOwner();
    }
}
