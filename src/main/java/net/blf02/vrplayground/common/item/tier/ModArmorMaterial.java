package net.blf02.vrplayground.common.item.tier;

import net.blf02.vrplayground.VRPlayground;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;

import java.util.function.Supplier;

public class ModArmorMaterial implements IArmorMaterial {

    private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityFactor;
    private final int[] armorValues;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Item> repairMaterial;

    public ModArmorMaterial(String id, int durabilityFactor, int[] armorValuesBootsToHelm, int enchantability,
                            SoundEvent equipSound, float toughness, float kbResistance, Supplier<Item> repairMaterial) {
        this.name = VRPlayground.MOD_ID + ":" + id;
        this.durabilityFactor = durabilityFactor;
        this.armorValues = armorValuesBootsToHelm;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = kbResistance;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotType) {
        return MAX_DAMAGE_ARRAY[slotType.getIndex()] * this.durabilityFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotType) {
        return this.armorValues[slotType.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(this.repairMaterial.get());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
