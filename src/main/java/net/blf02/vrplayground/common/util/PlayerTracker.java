package net.blf02.vrplayground.common.util;

import net.blf02.vrplayground.common.data.ForceInformation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerTracker {

    // Only actually used server-side. Used to track for immersive furnace.
    public static final Set<String> handsInFurnace = new HashSet<>();

    // Used for Force movement
    public static final Map<String, ForceInformation> forceInfo = new HashMap<>();
}
