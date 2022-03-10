package net.blf02.vrplayground.common.item;

import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.api.data.IVRPlayer;
import net.blf02.vrplayground.common.init.ItemInit;
import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.network.packet.EmptyRightClickPacket;
import net.blf02.vrplayground.common.util.ShootLaser;
import net.blf02.vrplayground.common.util.Util;
import net.blf02.vrplayground.common.vr.VRPlugin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class LaserHands extends Item {
    public LaserHands(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        // Return early if player isn't in VR.
        if (!VRPlugin.API.playerInVR(player)) return ActionResult.fail(player.getItemInHand(hand));

        if (level.isClientSide)  {
            // Tell the server we're right-clicking if we're wearing a laser helmet, so we shoot lasers from
            // our eyes when we shoot from our hands
            if (player.inventory.armor.get(3).getItem() == ItemInit.laserHelmet.get()) {
                Network.INSTANCE.sendToServer(new EmptyRightClickPacket());
            }

            // Rumble both controllers as we shoot our laser
            for (int i = 0; i <= 1; i++) { // For both controllers
                VRPlugin.API.triggerHapticPulse(i, 0.05f, null); // Rumble controller for 0.25 secs
            }

            Util.cancelRightClickCooldown(); // Cancel right click cooldown so we can immediately click again next tick

            return ActionResult.pass(player.getItemInHand(hand)); // Return if we're on the client side
        }

        // Below here, we're only running on the server-side/logical-side

        IVRPlayer vrPlayer = VRPlugin.API.getVRPlayer(player); // Get VR Player instance

        for (int i = 0; i <= 1; i++) { // Iterate over all controllers (0 and 1)
            IVRData controller = vrPlayer.getController(i); // Get the controller to shoot from

            ShootLaser.shootLaser(level, // Shoot a laser
                    controller.position(), // Using the controller's position
                    controller.getLookAngle(), // and where the controller is pointing
                    player);
        }

        return ActionResult.success(player.getItemInHand(hand));

    }
}
