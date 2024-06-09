package gregicality.science.common;

import gregicality.GCYSInternalTags;
import gregicality.science.api.unification.OrePrefixAdditions;
import gregicality.science.api.unification.materials.GCYSMaterials;
import gregicality.science.api.unification.materials.properties.GCYSMaterialPropertyAddition;
import gregtech.api.event.HighTierEvent;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = GCYSInternalTags.MODID)
public class GCYSEventHandlers {

    @SubscribeEvent
    public static void registerMaterials(@NotNull MaterialEvent event) {
        GCYSMaterials.init();
        GCYSMaterialPropertyAddition.init();
    }

    @SubscribeEvent
    public static void postRegisterMaterials(@NotNull PostMaterialEvent event) {
        OrePrefixAdditions.init();
    }

    @SubscribeEvent
    public void onHighTierEvent(HighTierEvent event) {
        event.enableHighTier();
    }
}
