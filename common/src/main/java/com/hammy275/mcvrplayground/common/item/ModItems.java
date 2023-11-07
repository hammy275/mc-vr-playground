package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.MCVRPlayground;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ModItems {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MCVRPlayground.MOD_ID, Registries.CREATIVE_MODE_TAB);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MCVRPlayground.MOD_ID, Registries.ITEM);
    public static final RegistrySupplier<CreativeModeTab> CREATIVE_TAB = TABS.register(
            "mc_vr_playground_tab", // Tab ID
            () -> CreativeTabRegistry.create(
                    Component.translatable("category." + MCVRPlayground.MOD_ID), // Tab Name
                    () -> new ItemStack(ModItems.ROCKET_HANDS.get()) // Icon
            )
    );

    public static final RegistrySupplier<Item> ROCKET_HANDS = ITEMS.register("rocket_hands", () ->
            new RocketHands(new Item.Properties().arch$tab(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> MAGIC_MISSILE = ITEMS.register("magic_missile", () ->
            new MagicMissileItem(new Item.Properties().arch$tab(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> HISTORY_VISUALIZER = ITEMS.register("history_visualizer", () ->
            new HistoryVisualizer(new Item.Properties().arch$tab(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> KEYBOARDINATOR = ITEMS.register("keyboardinator", () ->
            new KeyboardInatorItem(new Item.Properties().stacksTo(1).arch$tab(CREATIVE_TAB)));
    public static final RegistrySupplier<Item> DEBUG_INFO = ITEMS.register("debug_info", () ->
            new DebugInfoItem(new Item.Properties().stacksTo(1).arch$tab(CREATIVE_TAB)));

}
