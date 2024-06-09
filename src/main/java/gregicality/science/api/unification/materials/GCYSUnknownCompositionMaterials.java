package gregicality.science.api.unification.materials;

import gregtech.api.fluids.FluidBuilder;
import gregtech.api.unification.material.Material;

import static gregicality.science.api.unification.materials.GCYSMaterials.*;
import static gregicality.science.common.metatileentities.GCYSMetaTileEntities.gcysId;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.DISABLE_DECOMPOSITION;
import static gregtech.api.unification.material.info.MaterialIconSet.FINE;
import static gregtech.api.unification.material.info.MaterialIconSet.ROUGH;

public class GCYSUnknownCompositionMaterials {

    /**
     * 18000-19999
     */
    public static void init() {

        RareEarthHydroxidesSolution = new Material.Builder(18000, gcysId("rare_earth_hydroxides_solution"))
                .fluid()
                .color(0x434327)
                .flags(DISABLE_DECOMPOSITION)
                .components(RareEarth, 1, Oxygen, 1, Hydrogen, 1, Water, 1)
                .build();

        RareEarthChloridesSolution = new Material.Builder(18001, gcysId("rare_earth_chlorides_solution"))
                .fluid()
                .color(0x838367)
                .flags(DISABLE_DECOMPOSITION)
                .components(RareEarth, 1, Chlorine, 1, Water, 1)
                .build();

        LeachedTurpentine = new Material.Builder(18002, gcysId("leached_turpentine"))
                .fluid()
                .color(0x330D16)
                .flags(DISABLE_DECOMPOSITION)
                .components(Turpentine, 1, RareEarth, 1)
                .build();

        SteamCrackedTurpentine = new Material.Builder(18003, gcysId("steamcracked_turpentine"))
                .fluid()
                .color(0x634D56)
                .build();

        BZMedium = new Material.Builder(18004, gcysId("bz_medium"))
                .fluid()
                .color(0xA2FD35)
                .build(); //TODO "The Belousov-Zhabotinsky Reaction" tooltip

        RichNitrogenMixture = new Material.Builder(18013, gcysId("rich_nitrogen_mixture"))
                .gas()
                .color(0x6891D8)
                .build();

        RichAmmoniaMixture = new Material.Builder(18014, gcysId("rich_ammonia_mixture")).fluid().color(0x708ACD).build();

        Brine = new Material.Builder(18015, gcysId("brine"))
                .fluid()
                .color(0xFCFC8A)
                .build();

        ChlorinatedBrine = new Material.Builder(18016, gcysId("chlorinated_brine"))
                .fluid()
                .color(0xFAFC8A)
                .build();

        ChalcogenAnodeMud = new Material.Builder(18017, gcysId("chalcogen_anode_mud"))
                .dust()
                .color(0x8A3324)
                .iconSet(FINE)
                .build();

        MethylamineMixture = new Material.Builder(18018, gcysId("methylamine_mixture"))
                .fluid()
                .color(0xAA4400)
                .build();

        EDP = new Material.Builder(18019, gcysId("edp"))
                .fluid()
                .color(0xFBFF17)
                .build();

        PhosphoreneSolution = new Material.Builder(18020, gcysId("phosphorene_solution"))
                .fluid()
                .color(0x465966)
                .build();

        SodioIndene = new Material.Builder(18021, gcysId("sodio_indene"))
                .fluid()
                .color(0x1D1C24)
                .build();

        SteamCrackedSodioIndene = new Material.Builder(18022, gcysId("steam_cracked_sodio_indene"))
                .liquid(new FluidBuilder().temperature(1105))
                .color(0x1C1A29)
                .build();

        MolybdenumFlue = new Material.Builder(18023, gcysId("molybdenum_flue"))
                .gas()
                .color(0x39194A)
                .build();

        TraceRheniumFlue = new Material.Builder(18024, gcysId("trace_rhenium_flue"))
                .gas()
                .color(0x96D6D5)
                .build();

        FracturingFluid = new Material.Builder(18025, gcysId("fracturing_fluid"))
                .fluid()
                .color(0x96D6D5)
                .build();

        BedrockSmoke = new Material.Builder(18026, gcysId("bedrock_smoke"))
                .gas()
                .color(0x525252)
                .build();

        // FREE ID 18027

        Bedrock = new Material.Builder(18028, gcysId("bedrock"))
                .dust()
                .color(0x404040)
                .iconSet(ROUGH)
                .build();

        BedrockSootSolution = new Material.Builder(18029, gcysId("bedrock_soot_solution"))
                .fluid()
                .color(0x1E2430)
                .build();

        CleanBedrockSolution = new Material.Builder(18030, gcysId("clean_bedrock_solution"))
                .fluid()
                .color(0xA89F9E)
                .build();

        HeavyBedrockSmoke = new Material.Builder(18031, gcysId("heavy_bedrock_smoke"))
                .gas()
                .color(0x242222)
                .build();

        MediumBedrockSmoke = new Material.Builder(18032, gcysId("medium_bedrock_smoke"))
                .gas()
                .color(0x2E2C2C)
                .build();

        LightBedrockSmoke = new Material.Builder(18033, gcysId("light_bedrock_smoke"))
                .gas()
                .color(0x363333)
                .build();

        UltralightBedrockSmoke = new Material.Builder(18034, gcysId("ultralight_bedrock_smoke"))
                .gas()
                .color(0x403D3D)
                .build();

        HeavyTaraniumGas = new Material.Builder(18035, gcysId("heavy_taranium_gas"))
                .gas()
                .color(0x262626)
                .build();

        MediumTaraniumGas = new Material.Builder(18036, gcysId("medium_taranium_gas"))
                .gas()
                .color(0x313131)
                .build();

        LightTaraniumGas = new Material.Builder(18037, gcysId("light_taranium_gas"))
                .gas()
                .color(0x404040)
                .build();

        BedrockGas = new Material.Builder(18038, gcysId("bedrock_gas"))
                .gas()
                .color(0x575757)
                .build();

        CrackedHeavyTaranium = new Material.Builder(18039, gcysId("cracked_heavy_taranium"))
                .fluid()
                .color(0x1F2B2E)
                .build();

        CrackedMediumTaranium = new Material.Builder(18040, gcysId("cracked_medium_taranium"))
                .fluid()
                .color(0x29393D)
                .build();

        CrackedLightTaranium = new Material.Builder(18041, gcysId("cracked_light_taranium"))
                .fluid()
                .color(0x374C52)
                .build();

        EnrichedBedrockSootSolution = new Material.Builder(18042, gcysId("enriched_bedrock_soot_solution"))
                .fluid()
                .color(0x280C26)
                .build();

        CleanEnrichedBedrockSolution = new Material.Builder(18043, gcysId("clean_enriched_bedrock_solution"))
                .fluid()
                .color(0x828C8C)
                .build();

        HeavyEnrichedBedrockSmoke = new Material.Builder(18044, gcysId("heavy_enriched_bedrock_smoke"))
                .gas()
                .color(0x1A2222)
                .build();

        MediumEnrichedBedrockSmoke = new Material.Builder(18045, gcysId("medium_enriched_bedrock_smoke"))
                .gas()
                .color(0x1E2C2C)
                .build();

        LightEnrichedBedrockSmoke = new Material.Builder(18046, gcysId("light_enriched_bedrock_smoke"))
                .gas()
                .color(0x163333)
                .build();

        HeavyEnrichedTaraniumGas = new Material.Builder(18047, gcysId("heavy_enriched_taranium_gas"))
                .gas()
                .color(0x1F2626)
                .build();

        MediumEnrichedTaraniumGas = new Material.Builder(18048, gcysId("medium_enriched_taranium_gas"))
                .gas()
                .color(0x1F3131)
                .build();

        LightEnrichedTaraniumGas = new Material.Builder(18049, gcysId("light_enriched_taranium_gas"))
                .gas()
                .color(0x1F4040)
                .build();

        CrackedHeavyEnrichedTaranium = new Material.Builder(18050, gcysId("cracked_heavy_enriched_taranium"))
                .fluid()
                .color(0x2E1F2E)
                .build();

        CrackedMediumEnrichedTaranium = new Material.Builder(18051, gcysId("cracked_medium_enriched_taranium"))
                .fluid()
                .color(0x29393D)
                .build();

        CrackedLightEnrichedTaranium = new Material.Builder(18052, gcysId("cracked_light_enriched_taranium"))
                .fluid()
                .color(0x374C52)
                .build();

        EnergeticNaquadria = new Material.Builder(18053, gcysId("energetic_naquadria"))
                .fluid()
                .color(0x202020)
                .build();

        LightHyperFuel = new Material.Builder(18054, gcysId("light_hyper_fuel"))
                .fluid()
                .color(0x8C148C)
                .build();

        MediumHyperFuel = new Material.Builder(18055, gcysId("medium_hyper_fuel"))
                .fluid()
                .color(0xDC0A0A)
                .build();

        HeavyHyperFuel = new Material.Builder(18056, gcysId("heavy_hyper_fuel"))
                .fluid()
                .color(0x1E5064)
                .build();
    }
}
