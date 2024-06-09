package gregicality.science.intergration.theoneprobe;

import gregicality.GCYSInternalTags;
import gregicality.science.intergration.theoneprobe.provider.PressureContainerProvider;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.ITheOneProbe;
import org.apache.logging.log4j.LogManager;

public class TheOneProbeModule {

    public static void init() {
        LogManager.getLogger(GCYSInternalTags.MODNAME + " TOP Integration").info("TheOneProbe found. Enabling integration...");
        ITheOneProbe oneProbe = TheOneProbe.theOneProbeImp;
        oneProbe.registerProvider(new PressureContainerProvider());
    }
}
