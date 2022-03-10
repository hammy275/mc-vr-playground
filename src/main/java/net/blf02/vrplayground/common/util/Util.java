package net.blf02.vrplayground.common.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Util {

    @OnlyIn(Dist.CLIENT)
    public static void cancelRightClickCooldown() {
        Minecraft.getInstance().rightClickDelay = 0;
    }
}
