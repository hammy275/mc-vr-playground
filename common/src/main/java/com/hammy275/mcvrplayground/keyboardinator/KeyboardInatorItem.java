package com.hammy275.mcvrplayground.keyboardinator;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.client.data.OpenKeyboardContext;

/**
 * Basic item to force the keyboard to open when used.
 */
public class KeyboardInatorItem extends Item {
    public KeyboardInatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        boolean inVR = VRClientAPI.instance().isVRActive();
        if (inVR) {
            // Sets the keyboard to forcefully now be open.
            VRClientAPI.instance().openKeyboard(OpenKeyboardContext.FORCE);
        }
        return inVR ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
