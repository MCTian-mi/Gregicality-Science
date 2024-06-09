package gregicality.science.common;

import gregicality.science.GregicalityScience;
import gregicality.science.api.unification.OrePrefixAdditions;
import gregicality.science.api.unification.materials.GCYSMaterials;
import gregicality.science.api.unification.materials.properties.GCYSMaterialPropertyAddition;
import gregtech.api.unification.material.event.MaterialEvent;
import gregtech.api.unification.material.event.PostMaterialEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = GregicalityScience.MODID)
public class GCYSEventHandlers {

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerMaterials(@NotNull MaterialEvent event) {
        GCYSMaterials.init();
        GCYSMaterialPropertyAddition.init();
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void postRegisterMaterials(@NotNull PostMaterialEvent event) {
        OrePrefixAdditions.init();
    }
}
