package net.blf02.vrplayground.common.vr;

import net.blf02.vrapi.api.IVRAPI;
import net.blf02.vrapi.api.VRAPIPlugin;
import net.blf02.vrapi.api.VRAPIPluginProvider;

@VRAPIPlugin
public class VRPlugin implements VRAPIPluginProvider {

    // No need to split into two classes since this mod requires VR anyways

    public static IVRAPI API = null;

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        API = ivrapi;
    }
}
