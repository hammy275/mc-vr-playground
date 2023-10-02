package com.hammy275.mcvrplayground;

import com.hammy275.mcvrplayground.common.entity.ModEntities;
import com.hammy275.mcvrplayground.common.item.ModItems;

public class MCVRPlayground {

    public static final String MOD_ID = "mc_vr_playground";

    public static void init() {
        ModItems.TABS.register();
        ModItems.ITEMS.register();
        ModEntities.ENTITIES.register();
    }
}
