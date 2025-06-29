package com.hammy275.mcvrplayground.keyboardinator;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.vivecraft.api.client.VRClientAPI;

public class KeyboardInatorItem extends Item {
    public KeyboardInatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        boolean inVR = VRClientAPI.instance().isVRActive();
        if (inVR) {
            VRClientAPI.instance().setKeyboardState(true);
        }
        return inVR ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }
}
