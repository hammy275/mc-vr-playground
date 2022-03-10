package net.blf02.vrplayground.common.util;

import java.util.HashSet;
import java.util.Set;

public class PlayerTracker {

    // Only actually used server-side. Used to track for immersive furnace.
    public static final Set<String> handsInFurnace = new HashSet<>();
}
