package net.blf02.vrplayground.common.item;

import net.blf02.vrplayground.common.data.ForceInformation;
import net.blf02.vrplayground.common.util.PlayerTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class Force extends Item {

    public Force(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        if (level.isClientSide) return super.use(level, player, hand);
        ForceInformation info = PlayerTracker.forceInfo.get(player.getGameProfile().getName());
        if (info == null) {
            info = ForceInformation.createForceInteraction(player);
            if (info == null) {
                return super.use(level, player, hand);
            }
            PlayerTracker.forceInfo.put(player.getGameProfile().getName(), info);
        } else {
            PlayerTracker.forceInfo.remove(player.getGameProfile().getName());
            return super.use(level, player, hand);
        }

        return super.use(level, player, hand);
    }

    @Override
    public int getUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }
}
