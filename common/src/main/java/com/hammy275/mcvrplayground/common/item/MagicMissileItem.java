package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.common.entity.MagicMissileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.vivecraft.api.VRAPI;

public class MagicMissileItem extends Item {

    public MagicMissileItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (VRAPI.instance().isVRPlayer(player)) {
            MagicMissileEntity.create(player);
            player.getCooldowns().addCooldown(player.getItemInHand(interactionHand), 100);
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }
}
