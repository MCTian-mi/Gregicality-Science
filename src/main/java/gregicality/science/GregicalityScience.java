package gregicality.science;

import gregicality.GCYSInternalTags;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.utils.GCYSLog;
import gregicality.science.common.CommonProxy;
import gregicality.science.common.block.GCYSMetaBlocks;
import gregicality.science.common.items.GCYSMetaItems;
import gregicality.science.common.metatileentities.GCYSMetaTileEntities;
import gregicality.science.intergration.theoneprobe.TheOneProbeModule;
import gregtech.GTInternalTags;
import gregtech.api.event.HighTierEvent;
import gregtech.api.util.Mods;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@Mod(modid = GregicalityScience.MODID,
        name = GregicalityScience.NAME,
        version = GregicalityScience.VERSION,
        dependencies = GTInternalTags.DEP_VERSION_STRING + "required-after:gcym")
public class GregicalityScience {

    public static final String MODID = GCYSInternalTags.MODID;
    public static final String NAME = GCYSInternalTags.MODNAME;
    public static final String VERSION = GCYSInternalTags.VERSION;

    @SidedProxy(modId = GregicalityScience.MODID,
            clientSide = "gregicality.science.common.ClientProxy",
            serverSide = "gregicality.science.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void onHighTierEvent(HighTierEvent event) {
        event.enableHighTier();
    }

    @Mod.EventHandler
    public void onPreInit(@Nonnull FMLPreInitializationEvent event) {
        GCYSLog.init(event.getModLog());

        GCYSTileCapabilities.init();

        GCYSMetaItems.initMetaItems();
        GCYSMetaBlocks.init();
        GCYSMetaTileEntities.init();

        proxy.preLoad();
    }

    @Mod.EventHandler
    public void onInit(@NotNull FMLInitializationEvent event) {
        if (Mods.TheOneProbe.isModLoaded()) {
            TheOneProbeModule.init();
        }
    }
}
