package net.blf02.vrplayground.common.init;

import net.blf02.vrplayground.VRPlayground;
import net.blf02.vrplayground.common.item.Force;
import net.blf02.vrplayground.common.item.LaserHands;
import net.blf02.vrplayground.common.item.RocketHands;
import net.blf02.vrplayground.common.item.tier.ModArmorMaterial;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VRPlayground.MOD_ID);

    public static final RegistryObject<Item> rocketHands = ITEMS.register("rocket_hands", () ->
            new RocketHands(new Item.Properties().stacksTo(1).tab(VRPlayground.creativeGroup)));
    public static final RegistryObject<Item> laserHands = ITEMS.register("laser_hands", () ->
            new LaserHands(new Item.Properties().stacksTo(1).tab(VRPlayground.creativeGroup)));

    public static final IArmorMaterial laserHelmetMat = new ModArmorMaterial("laser", 20, new int[]{1, 3, 4, 2},
            1, SoundEvents.ARMOR_EQUIP_IRON, 0, 0, () -> Items.IRON_INGOT);
    public static final RegistryObject<Item> laserHelmet = ITEMS.register("laser_helmet", () ->
            new ArmorItem(laserHelmetMat, EquipmentSlotType.HEAD,
                    new Item.Properties().tab(VRPlayground.creativeGroup)));

    public static final RegistryObject<Item> force = ITEMS.register("force", () ->
            new Force(new Item.Properties().tab(VRPlayground.creativeGroup).stacksTo(1)));

}
