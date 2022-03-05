package net.blf02.vrplayground.common.item;

import net.blf02.vrapi.api.data.IVRPlayer;
import net.blf02.vrplayground.common.vr.VRPlugin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RocketHands extends Item {

    public static final double mult = -0.25;

    public RocketHands(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player);
        Vector3d c0 = vrPlayer.getController0().getLookVec().multiply(mult, mult, mult);
        Vector3d c1 = vrPlayer.getController1().getLookVec().multiply(mult, mult, mult);
        player.setDeltaMovement(player.getDeltaMovement().add(c0).add(c1));
        if (player.getDeltaMovement().y >= 0) {
            player.fallDistance = 0; // Reset fall distance if we're going up
        }
        return ActionResult.fail(player.getItemInHand(hand));
    }
}