package net.blf02.vrplayground.common;

import net.blf02.vrapi.api.data.IVRData;
import net.blf02.vrapi.common.network.packets.VRDataPacket;
import net.blf02.vrapi.event.VRPlayerTickEvent;
import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.network.packet.EmptyRightClickPacket;
import net.blf02.vrplayground.common.util.PlayerTracker;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonSubscriber {

    @SubscribeEvent
    public void emptyRightClick(PlayerInteractEvent.RightClickEmpty event) {
        Network.INSTANCE.sendToServer(new EmptyRightClickPacket());
    }

    @SubscribeEvent
    public void rightClickOnBlock(PlayerInteractEvent.RightClickBlock event) {
        // Also send right click if the hand is empty
        if (event.getPlayer().getItemInHand(Hand.MAIN_HAND) == ItemStack.EMPTY) {
            Network.INSTANCE.sendToServer(new EmptyRightClickPacket());
        }
    }

    @SubscribeEvent
    public void immersiveFurnace(VRPlayerTickEvent event) {
        Vector3d controller = event.vrPlayer.getController0().position(); // Get info about main controller position
        BlockPos pos = new BlockPos(controller); // Get block position from controller position
        ItemStack stack = event.player.getMainHandItem(); // Get held item

        // Immersive furnace code
        Block toCheck = event.player.level.getBlockState(pos).getBlock();
        if ((toCheck == Blocks.FURNACE || toCheck == Blocks.SMOKER || toCheck == Blocks.BLAST_FURNACE) &&
                !PlayerTracker.handsInFurnace.contains(event.player.getGameProfile().getName())) {
            // Get tile entity for furnace
            AbstractFurnaceTileEntity tileEnt = (AbstractFurnaceTileEntity) event.player.level.getBlockEntity(pos);
            /* Mark player as currently using furnace (prevents a player from using the furnace multiple times
               without taking their hand out first) */
            PlayerTracker.handsInFurnace.add(event.player.getGameProfile().getName());

            if (event.player.getMainHandItem() == ItemStack.EMPTY) {
                /* If the player's hand is empty, and there is something in the output slot, we can
                   put it in the player's hand. */
                if (!tileEnt.getItem(2).getStack().isEmpty()) {
                    event.player.setItemInHand(Hand.MAIN_HAND, tileEnt.getItem(2).getStack());
                    tileEnt.setItem(2, ItemStack.EMPTY);
                }
            } else if (tileEnt.getItem(1).getStack().isEmpty()
                    && tileEnt.canPlaceItem(1, stack)) {
                // If the held item by the player is valid fuel, and the fuel slot is empty, put it in the fuel slot
                tileEnt.setItem(1, stack);
                event.player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
            else if (tileEnt.getItem(0).getStack().isEmpty()) {
                // If the item to be smelted slot is empty and the player is holding something, put that something there
                tileEnt.setItem(0, stack);
                event.player.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
            }
        } else if (toCheck != Blocks.FURNACE && toCheck != Blocks.SMOKER && toCheck != Blocks.BLAST_FURNACE) {
            // Mark player as not using furnace if their hand isn't in a furnace anymore
            PlayerTracker.handsInFurnace.remove(event.player.getGameProfile().getName());
        }
    }
}
