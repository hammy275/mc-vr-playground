package net.blf02.vrplayground.common.vr;

import net.blf02.vrapi.api.IVRAPI;
import net.blf02.vrapi.api.VRAPIPlugin;
import net.blf02.vrapi.api.VRAPIPluginProvider;
import net.blf02.vrplayground.common.subscribe.CommonSubscriber;
import net.minecraftforge.common.MinecraftForge;

@VRAPIPlugin
public class VRPlugin implements VRAPIPluginProvider {

    // No need to split into two classes since this mod requires VR anyways
    // Generally, you'd create a separate `VRPluginChecker` class with a `isVRLoaded` variable set to false.
    // Then, inside of `getVRAPI()`, you'd set that variable to true.

    public static IVRAPI API = null; // Where the API instance will go

    @Override
    public void getVRAPI(IVRAPI ivrapi) {
        API = ivrapi; // Set our VR API instance so our mod can use it
        //VRPluginChecker.isVRLoaded = true;

        // Since our subscriber uses VR events, we should only register it if the API is detected
        MinecraftForge.EVENT_BUS.register(new CommonSubscriber());
    }
}
