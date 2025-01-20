package com.hammy275.mcvrplayground.common.entity;

import net.minecraft.world.entity.projectile.ItemSupplier;

public interface ScaledItemSupplier extends ItemSupplier {
    float getRoll();

    float getScale();
}
