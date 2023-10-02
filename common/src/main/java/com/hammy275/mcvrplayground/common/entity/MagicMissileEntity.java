package com.hammy275.mcvrplayground.common.entity;

import com.hammy275.mcvrplayground.common.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.api.VivecraftAPI;
import org.vivecraft.api.data.VRPose;

public class MagicMissileEntity extends Projectile implements ItemSupplier {
    public static final EntityDataAccessor<Float> ROLL = SynchedEntityData.defineId(MagicMissileEntity.class,
            EntityDataSerializers.FLOAT); // Using a FLOAT here since there's no built-in DOUBLE from Vanilla MC.

    public MagicMissileEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    public static MagicMissileEntity create(Player owner) {
        if (VivecraftAPI.getInstance().isVRPlayer(owner)) {
            VRPose hand = VivecraftAPI.getInstance().getVRData(owner).getController0();

            MagicMissileEntity missile = new MagicMissileEntity(ModEntities.magicMissile.get(), owner.level());
            // Move missile 1 block ahead of player's hand in the direction the hand is pointing
            missile.setPos(hand.getPos().add(hand.getRot()));

            missile.setOwner(owner);
            owner.level().addFreshEntity(missile);
            missile.entityData.set(ROLL, (float) hand.getRoll()); // This is cast to float, since ROLL expects a float
            return missile;
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() instanceof Player owner && VivecraftAPI.getInstance().isVRPlayer(owner)) {
            // Owner is online and in VR!
            VRPose hand = VivecraftAPI.getInstance().getVRData(owner).getController0();
            // Set the roll value of this entity to the roll of the controller
            this.entityData.set(ROLL, (float) hand.getRoll()); // This is cast to float, since ROLL expects a float
            // Move 0.2 blocks in direction that hand is pointing
            this.moveTo(this.position().add(hand.getRot().scale(0.2)));
            // Set the delta movement for hit detection
            this.setDeltaMovement(hand.getRot().scale(0.2));
        } else {
            this.discard(); // Remove if projectile owner is no longer online or leaves VR
        }


        HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitResult.getType() != HitResult.Type.MISS && !this.isRemoved()) {
            this.onHit(hitResult);
        }
        // Set the delta movement back to 0, since the position is directly controlled
        this.setDeltaMovement(0, 0, 0);

        if (this.level().isClientSide) {
            int numParticles = this.random.nextInt(5);
            for (int i = 0; i < numParticles; i++) {
                this.level().addParticle(
                        ParticleTypes.ENCHANTED_HIT,
                        this.getX(), this.getY() + this.getBbHeight() / 2f, this.getZ(),
                        0.5, 0.5, 0.5
                );
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
        victim.hurt(damageSource, 10f); // Deal 10 damage to victim
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROLL, 0f);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.MAGIC_MISSILE.get());
    }
}
