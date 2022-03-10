package net.blf02.vrplayground.common.subscribe;

import net.blf02.vrapi.event.VRPlayerTickEvent;
import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.network.packet.EmptyRightClickPacket;
import net.blf02.vrplayground.common.util.PlayerTracker;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonSubscriber {

    @SubscribeEvent
    public void emptyRightClick(PlayerInteractEvent.RightClickEmpty event) {
        Network.INSTANCE.sendToServer(new EmptyRightClickPacket());
    }

    @SubscribeEvent
    public void immersiveFurnace(VRPlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT) return; // Only process server-side to prevent de-syncs
        for (int i = 0; i <= 1; i++) { // For both controllers
            Vector3d controller = event.vrPlayer.getController(i).position(); // Get info about controller position
            BlockPos pos = new BlockPos(controller); // Get block position from controller position
            Hand hand = i == 0 ? Hand.MAIN_HAND : Hand.OFF_HAND; // Get hand based on controller
            ItemStack stack = event.player.getItemInHand(hand); // Get held item
            // The String the tracker uses to determine if that hand is in the furnace or not.
            // We use the username separated by a space, then the controller number so each
            // controller can be tracked separately. Use a space specifically since usernames
            // can't have spaces.
            String trackerInfo = event.player.getGameProfile().getName() + " " + i;

            // Immersive furnace code
            Block toCheck = event.player.level.getBlockState(pos).getBlock();
            if ((toCheck == Blocks.FURNACE || toCheck == Blocks.SMOKER || toCheck == Blocks.BLAST_FURNACE) &&
                    !PlayerTracker.handsInFurnace.contains(trackerInfo)) {
                // Get tile entity for furnace
                AbstractFurnaceTileEntity tileEnt = (AbstractFurnaceTileEntity) event.player.level.getBlockEntity(pos);
            /* Mark player as currently using furnace (prevents a player from using the furnace multiple times
               without taking their hand out first) */
                PlayerTracker.handsInFurnace.add(trackerInfo);

                if (event.player.getItemInHand(hand) == ItemStack.EMPTY) {
                /* If the player's hand is empty, and there is something in the output slot, we can
                   put it in the player's hand. */
                    if (!tileEnt.getItem(2).getStack().isEmpty()) {
                        event.player.setItemInHand(hand, tileEnt.getItem(2).getStack());
                        tileEnt.setItem(2, ItemStack.EMPTY);
                    }
                } else if (tileEnt.getItem(1).getStack().isEmpty()
                        && tileEnt.canPlaceItem(1, stack)) {
                    // If the held item by the player is valid fuel, and the fuel slot is empty, put it in the fuel slot
                    tileEnt.setItem(1, stack);
                    event.player.setItemInHand(hand, ItemStack.EMPTY);
                }
                else if (tileEnt.getItem(0).getStack().isEmpty()) {
                    // If the item to be smelted slot is empty and the player is holding something, put that something there
                    tileEnt.setItem(0, stack);
                    event.player.setItemInHand(hand, ItemStack.EMPTY);
                }
            } else if (toCheck != Blocks.FURNACE && toCheck != Blocks.SMOKER && toCheck != Blocks.BLAST_FURNACE) {
                // Mark player as not using furnace if their hand isn't in a furnace anymore
                PlayerTracker.handsInFurnace.remove(trackerInfo);
            }
        }
    }
}
