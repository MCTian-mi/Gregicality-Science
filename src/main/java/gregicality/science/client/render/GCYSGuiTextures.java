package gregicality.science.client.render;

import gregicality.GCYSInternalTags;
import gregtech.api.gui.resources.TextureArea;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = GCYSInternalTags.MODID, value = Side.CLIENT)
public class GCYSGuiTextures {

    public static final TextureArea PRESSURE_INDICATOR = TextureArea.fullImage("textures/gui/icon/pressure_indicator.png");
    public static final TextureArea NANOSCALE_OVERLAY_1 = TextureArea.fullImage("textures/gui/overlay/nanoscale_overlay_1.png");
    public static final TextureArea NANOSCALE_OVERLAY_2 = TextureArea.fullImage("textures/gui/overlay/nanoscale_overlay_2.png");
    public static final TextureArea FOIL_OVERLAY = TextureArea.fullImage("textures/gui/overlay/foil_overlay.png");
    public static TextureArea PROGRESS_BAR_NANOSCALE = TextureArea.fullImage("textures/gui/progress_bar/progress_bar_nanoscale.png");

}
