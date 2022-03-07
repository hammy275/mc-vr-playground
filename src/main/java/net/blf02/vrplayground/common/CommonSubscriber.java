package net.blf02.vrplayground.common;

import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.network.packet.EmptyRightClickPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
}
