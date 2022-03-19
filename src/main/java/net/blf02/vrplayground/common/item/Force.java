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
        if (level.isClientSide) return ActionResult.pass(player.getItemInHand(hand)); // Return if on client side
        // Gets information about the currently active force instance (if one exists)
        ForceInformation info = PlayerTracker.forceInfo.get(player.getGameProfile().getName());

        if (info == null) {
            info = ForceInformation.createForceInteraction(player); // If we don't have an instance, create one
            if (info == null) {
                // If we still don't have one, we couldn't create an instance (player isn't in VR or isn't
                // targeting any mobs)
                return ActionResult.pass(player.getItemInHand(hand));
            }
            // Add our instance to the tracker
            PlayerTracker.forceInfo.put(player.getGameProfile().getName(), info);
        } else {
            // If we're already using the force, we remove it when the player right clicks again
            PlayerTracker.forceInfo.remove(player.getGameProfile().getName());
            return ActionResult.pass(player.getItemInHand(hand));
        }
        return super.use(level, player, hand);
    }
}
