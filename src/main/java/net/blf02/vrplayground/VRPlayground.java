package net.blf02.vrplayground;

import net.blf02.vrplayground.common.CommonSubscriber;
import net.blf02.vrplayground.common.init.ItemInit;
import net.blf02.vrplayground.common.network.Network;
import net.blf02.vrplayground.common.network.packet.EmptyRightClickPacket;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VRPlayground.MOD_ID)
public class VRPlayground {

    public static final String MOD_ID = "vrplayground";

    public static final ItemGroup creativeGroup = new PlaygroundGroup();

    public VRPlayground() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);

        ItemInit.ITEMS.register(bus);

        MinecraftForge.EVENT_BUS.register(new CommonSubscriber());
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        int index = 1;
        Network.INSTANCE.registerMessage(index++, EmptyRightClickPacket.class, EmptyRightClickPacket::encode,
                EmptyRightClickPacket::decode, EmptyRightClickPacket::handle);
    }

    public static class PlaygroundGroup extends ItemGroup {

        public PlaygroundGroup() {
            super("playground_tab");
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.rocketHands.get());
        }
    }

}
