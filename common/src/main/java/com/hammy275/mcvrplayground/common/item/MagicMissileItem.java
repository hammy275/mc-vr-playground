package com.hammy275.mcvrplayground.common.item;

import com.hammy275.mcvrplayground.common.entity.MagicMissileEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.vivecraft.api.VivecraftAPI;

public class MagicMissileItem extends Item {

    public MagicMissileItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        if (VivecraftAPI.getInstance().isVRPlayer(player)) {
            MagicMissileEntity.create(player);
            player.getCooldowns().addCooldown(this, 100);
            return InteractionResultHolder.success(player.getItemInHand(interactionHand));
        } else {
            return InteractionResultHolder.fail(player.getItemInHand(interactionHand));
        }
    }
}
