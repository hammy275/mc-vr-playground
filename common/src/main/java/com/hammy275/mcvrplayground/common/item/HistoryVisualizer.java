package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.client.proxy.HistoryVisualizerClientTick;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.api.client.VivecraftClientAPI;

import java.util.List;

public class HistoryVisualizer extends Item {

    private static final String MODE_KEY = "visualizer_mode";

    public HistoryVisualizer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        nextMode(player.getItemInHand(interactionHand), player);
        return InteractionResultHolder.success(player.getItemInHand(interactionHand));

    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);

        // Check that we are on the client side and that the player is in VR before attempting to visualize history
        if (entity.level().isClientSide &&
                VivecraftClientAPI.getInstance().isVrActive()) {
            HistoryVisualizerClientTick.tick(itemStack, entity); // Do the actual logic
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        // Displays a useful description in-game
        list.add(Component.translatable("item.mc_vr_playground.history_visualizer.visualizing." + getMode(itemStack).ordinal()));
        list.add(Component.translatable("item.mc_vr_playground.history_visualizer.right_click"));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    private static void nextMode(ItemStack itemStack, Player player) {
        int newMode = getMode(itemStack).ordinal() + 1;
        if (newMode == VisualizerMode.values().length) {
            newMode = 0;
        }

        CompoundTag nbt = itemStack.getTag();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        nbt.putInt(MODE_KEY, newMode);
        itemStack.setTag(nbt);

        if (player.level().isClientSide) {
            player.sendSystemMessage(Component.translatable("item.mc_vr_playground.history_visualizer.visualizing." + newMode));
        }
    }

    public static VisualizerMode getMode(ItemStack itemStack) {
        // Get the current device being visualized from NBT, with a default of controller 0 if not set.
        CompoundTag nbt = itemStack.getTag();
        if (nbt == null || !nbt.contains(MODE_KEY)) {
            return VisualizerMode.Controller0;
        }
        // Modulo 3 so invalid values set by commands give us something sensible
        return VisualizerMode.values()[nbt.getInt(MODE_KEY) % 3];
    }

    public enum VisualizerMode {
        Controller0, Controller1, HMD
    }
}
