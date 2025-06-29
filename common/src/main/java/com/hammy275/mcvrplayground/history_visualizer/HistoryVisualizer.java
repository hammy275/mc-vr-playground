package com.hammy275.mcvrplayground.history_visualizer;

import com.hammy275.mcvrplayground.shared.ModComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;

import java.util.List;

/**
 * Item used to visualize the position of individual VR body parts. See
 * {@link HistoryVisualizerClientTick#tick(ItemStack, Entity)} for the actual visualization logic.
 */
public class HistoryVisualizer extends Item {

    public HistoryVisualizer(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        nextBodyPart(player.getItemInHand(interactionHand), player);
        return InteractionResult.SUCCESS;

    }

    @Override
    public void inventoryTick(ItemStack itemStack, Level level, Entity entity, int i, boolean bl) {
        super.inventoryTick(itemStack, level, entity, i, bl);

        // Check that we are on the client side and that the player is in VR before attempting to visualize history
        if (entity.level().isClientSide &&
                VRClientAPI.instance().isVRActive()) {
            HistoryVisualizerClientTick.tick(itemStack, entity); // Do the actual logic
        }
    }


    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        // Displays a useful description in-game
        list.add(Component.translatable("item.mc_vr_playground.history_visualizer.visualizing", getBodyPart(itemStack)));
        list.add(Component.translatable("item.mc_vr_playground.history_visualizer.right_click"));
        super.appendHoverText(itemStack, tooltipContext, list, tooltipFlag);
    }

    private static void nextBodyPart(ItemStack itemStack, Player player) {
        int newBodyPart = getBodyPart(itemStack).ordinal() + 1;
        if (newBodyPart == VRBodyPart.values().length) {
            newBodyPart = 0;
        }

        itemStack.set(ModComponents.HISTORY_VISUALIZER_COMPONENT.get(), newBodyPart);

        if (player.level().isClientSide) {
            player.displayClientMessage(Component.translatable("item.mc_vr_playground.history_visualizer.visualizing", getBodyPart(itemStack)), false);
        }
    }

    public static VRBodyPart getBodyPart(ItemStack itemStack) {
        // Get the current device being visualized from the item components, with a default of controller 0 if not set.
        Integer bodyPart = itemStack.get(ModComponents.HISTORY_VISUALIZER_COMPONENT.get());
        if (bodyPart == null) {
            bodyPart = 0;
        }
        // Modulo by total number of body parts so invalid values give us something sensible
        return VRBodyPart.values()[bodyPart % VRBodyPart.values().length];
    }
}
