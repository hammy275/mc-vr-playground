package com.hammy275.mcvrplayground.common.item.component;

import com.hammy275.mcvrplayground.MCVRPlayground;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class ModComponents {

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(MCVRPlayground.MOD_ID, Registries.DATA_COMPONENT_TYPE);


    public static final RegistrySupplier<DataComponentType<Integer>> HISTORY_VISUALIZER_COMPONENT = COMPONENTS.register(
            ResourceLocation.fromNamespaceAndPath(MCVRPlayground.MOD_ID, "history_visualizer"),
            () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

}
