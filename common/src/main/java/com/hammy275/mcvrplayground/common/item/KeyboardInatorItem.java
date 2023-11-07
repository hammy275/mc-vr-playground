package com.hammy275.mcvrplayground.common.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VivecraftClientAPI;

public class KeyboardInatorItem extends Item {
    public KeyboardInatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        boolean inVR = VivecraftClientAPI.getInstance().isVrActive();
        if (inVR) {
            VivecraftClientAPI.getInstance().setKeyboardState(true);
        }
        return inVR ? InteractionResultHolder.success(player.getItemInHand(interactionHand)) :
                InteractionResultHolder.pass(player.getItemInHand(interactionHand));
    }
}
