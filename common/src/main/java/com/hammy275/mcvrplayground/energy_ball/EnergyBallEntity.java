package com.hammy275.mcvrplayground.energy_ball;

import com.hammy275.mcvrplayground.shared.ModEntities;
import com.hammy275.mcvrplayground.shared.ScaledItemSupplier;
import com.hammy275.mcvrplayground.shared.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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

/**
 * The entity for the energy ball. The ball's position while charging is controlled by the energy ball item in
 * {@link EnergyBallItem#onUseTick(Level, LivingEntity, ItemStack, int)}.
 */
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

    /**
     * Creates a new energy ball entity from a player in VR.
     *
     * @param owner The VR player to create the energy ball for.
     * @param centerPos The position to create the energy ball at.
     * @return The created energy ball.
     */
    public static EnergyBallEntity createFromVRPlayer(Player owner, Vec3 centerPos) {
        EnergyBallEntity energyBall = new EnergyBallEntity(ModEntities.energyBall.get(), owner.level());
        energyBall.setOwner(owner);
        energyBall.setPos(centerPos);
        energyBall.entityData.set(SCALE, 0.5f);
        owner.level().addFreshEntity(energyBall);
        return energyBall;
    }

    /**
     * Get the nearest energy ball to the player that the player controls.
     * <br>
     * This is used to both control the position of a pre-existing ball and to make sure a player doesn't make
     * more than one ball at a time.
     *
     * @param player The player to get the nearest energy ball for.
     * @return The nearest energy ball that the provided player controls, or an empty {@link Optional} if no such ball
     * exists.
     */
    public static Optional<EnergyBallEntity> getNearbyBall(Player player) {
        List<EnergyBallEntity> energyBalls = player.level().getEntitiesOfClass(EnergyBallEntity.class,
                AABB.ofSize(player.getEyePosition(), 10, 10, 10), ball -> ball.getOwner() == player);
        return energyBalls.isEmpty() ? Optional.empty() : Optional.of(energyBalls.getFirst());
    }

    @Override
    public void tick() {
        super.tick();
        this.setPos(this.position().add(this.getDeltaMovement()));
        // Handle hit detection like a projectile (as a point changing from the last tick to this tick).
        if (this.energyBallShot()) {
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS && !this.isRemoved()) {
                this.onHit(hitResult);
            }
            // Handle hit detection for the entire hitbox (the entire hitbox on only this tick.
            if (!this.isRemoved()) {
                float scale = this.getScale();
                List<Entity> entitiesInBox = this.level().getEntities(this.getOwner(), AABB.ofSize(this.getEyePosition(), scale, scale, scale),
                        this::canHitEntity);
                if (!entitiesInBox.isEmpty()) {
                    onHit(new EntityHitResult(entitiesInBox.getFirst()));
                }
            }
            // Discard if ball has been shot for 5 seconds.
            if (++this.ticksShot > 100) {
                this.discard();
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        // Discard the energy ball when it hits something.
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
        builder.define(SCALE, 0.5f); // The energy ball grows the more its charged, increasing its scale.
        builder.define(SHOT, false); // Whether the energy ball has been shot (true) or is still player-controlled (false)
    }

    public boolean energyBallShot() {
        return this.entityData.get(SHOT);
    }

    public void grow() {
        this.entityData.set(SCALE, Math.min(this.getScale() + 0.05f, 3f));
    }

    /**
     * Shoot the energy ball.
     * @param shootVec The vector to shoot the ball at. Tracing this back through the packet originally sent, this
     *                 is the average velocity of the main-hand and the off-hand from the past 5 ticks.
     */
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
