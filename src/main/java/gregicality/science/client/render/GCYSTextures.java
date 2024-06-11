package gregicality.science.client.render;

import gregicality.GCYSInternalTags;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.client.renderer.texture.cube.SimpleOverlayRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static gregtech.api.util.GTUtility.gregtechId;

@Mod.EventBusSubscriber(modid = GCYSInternalTags.MODID, value = Side.CLIENT)
public class GCYSTextures {

    public static OrientedOverlayRenderer DRYER_OVERLAY = new OrientedOverlayRenderer("machines/dryer");
    public static OrientedOverlayRenderer CRYSTALLIZATION_CRUCIBLE_OVERLAY = new OrientedOverlayRenderer("multiblock/crystallization_crucible");
    public static OrientedOverlayRenderer ROASTER_OVERLAY = new OrientedOverlayRenderer("multiblock/roaster");
    public static OrientedOverlayRenderer NANOSCALE_FABRICATOR_OVERLAY = new OrientedOverlayRenderer("multiblock/nanoscale_fabricator");
    public static OrientedOverlayRenderer CVD_UNIT_OVERLAY = new OrientedOverlayRenderer("multiblock/cvd_unit");
    public static OrientedOverlayRenderer BURNER_REACTOR_OVERLAY = new OrientedOverlayRenderer("multiblock/burner_reactor");
    public static OrientedOverlayRenderer CRYOGENIC_REACTOR_OVERLAY = new OrientedOverlayRenderer("multiblock/cryogenic_reactor");
    public static OrientedOverlayRenderer FRACKER_OVERLAY = new OrientedOverlayRenderer("multiblock/fracker");
    public static OrientedOverlayRenderer SONICATOR_OVERLAY = new OrientedOverlayRenderer("multiblock/sonicator");
    public static OrientedOverlayRenderer CATALYTIC_REFORMER_OVERLAY = new OrientedOverlayRenderer("multiblock/catalytic_reformer");
    public static OrientedOverlayRenderer INDUSTRIAL_DRILL_OVERLAY = new OrientedOverlayRenderer("multiblock/industrial_drill");
    public static OrientedOverlayRenderer SUBSONIC_AXIAL_COMPRESSOR_OVERLAY = new OrientedOverlayRenderer("multiblock/subsonic_axial_compressor");
    public static OrientedOverlayRenderer SUPERSONIC_AXIAL_COMPRESSOR_OVERLAY = new OrientedOverlayRenderer("multiblock/supersonic_axial_compressor");
    public static OrientedOverlayRenderer LOW_POWER_TURBOMOLECULAR_PUMP = new OrientedOverlayRenderer("multiblock/low_power_turbomolecular_pump");
    public static OrientedOverlayRenderer HIGH_POWER_TURBOMOLECULAR_PUMP = new OrientedOverlayRenderer("multiblock/high_power_turbomolecular_pump");

    public static SimpleOverlayRenderer INFINITE_PRESSURE_PUMP_OVERLAY = new SimpleOverlayRenderer("overlay/machine/overlay_infinite_pump");

    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite SEALED_OVERLAY;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite PIPE_PRESSURE_SIDE;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite PIPE_PRESSURE_TINY;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite PIPE_PRESSURE_SMALL;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite PIPE_PRESSURE_NORMAL;
    @SideOnly(Side.CLIENT)
    public static TextureAtlasSprite PIPE_PRESSURE_LARGE;

    @SideOnly(Side.CLIENT)
    public static void register(TextureMap textureMap) {
        SEALED_OVERLAY = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_sealed"));
        PIPE_PRESSURE_SIDE = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_side"));
        PIPE_PRESSURE_TINY = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_tiny_in"));
        PIPE_PRESSURE_SMALL = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_small_in"));
        PIPE_PRESSURE_NORMAL = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_normal_in"));
        PIPE_PRESSURE_LARGE = textureMap.registerSprite(gregtechId("blocks/pipe/pipe_pressure_large_in"));
    }
}
