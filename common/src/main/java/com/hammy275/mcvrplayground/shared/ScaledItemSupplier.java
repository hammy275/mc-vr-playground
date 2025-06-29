package com.hammy275.mcvrplayground.shared;

import net.minecraft.world.entity.projectile.ItemSupplier;

public interface ScaledItemSupplier extends ItemSupplier {
    float getRoll();

    float getScale();
}
