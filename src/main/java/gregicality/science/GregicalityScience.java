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
import gregtech.api.util.Mods;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@Mod(modid = GCYSInternalTags.MODID,
        name = GCYSInternalTags.MODNAME,
        version = GCYSInternalTags.VERSION,
        dependencies = GTInternalTags.DEP_VERSION_STRING + "required-after:gcym")
public class GregicalityScience {

    @SidedProxy(modId = GCYSInternalTags.MODID,
            clientSide = "gregicality.science.common.ClientProxy",
            serverSide = "gregicality.science.common.CommonProxy")
    public static CommonProxy proxy;


    @EventHandler
    public void onPreInit(@Nonnull FMLPreInitializationEvent event) {
        GCYSLog.init(event.getModLog());

        GCYSTileCapabilities.init();

        GCYSMetaItems.initMetaItems();
        GCYSMetaBlocks.init();
        GCYSMetaTileEntities.init();

        proxy.preLoad();
    }

    @EventHandler
    public void onInit(@NotNull FMLInitializationEvent event) {
        if (Mods.TheOneProbe.isModLoaded()) {
            TheOneProbeModule.init();
        }
    }
}
