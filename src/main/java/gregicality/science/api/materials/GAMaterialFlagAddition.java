package gregicality.science.api.materials;

import gregtech.api.unification.material.info.MaterialFlag;
import gregtech.api.unification.material.properties.*;
import gregtech.api.unification.ore.OrePrefix;

import static gregicality.science.api.GCYSciMaterials.*;
import static gregicality.science.api.materials.GAMaterialFlags.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.material.info.MaterialFlags.*;

public class GAMaterialFlagAddition {

    public static void init() {
        initGeneral();
        initOres();
        initNuclearMaterial();
    }

    private static void initGeneral() {
        /* TODO
        ignoreCable(UHVSuperconductor);
        ignoreCable(UEVSuperconductor);
        ignoreCable(UIVSuperconductor);
        ignoreCable(UMVSuperconductor);
        ignoreCable(UXVSuperconductor);

        addCableAboveGTCELimit(UHVSuperconductorBase, new WireProperties(GAValues.V[GAValues.UHV], 4, 2));
        addCableAboveGTCELimit(UEVSuperconductorBase, new WireProperties(GAValues.V[GAValues.UEV], 4, 2));
        addCableAboveGTCELimit(UIVSuperconductorBase, new WireProperties(GAValues.V[GAValues.UIV], 4, 2));
        addCableAboveGTCELimit(UMVSuperconductorBase, new WireProperties(GAValues.V[GAValues.UMV], 4, 2));
        addCableAboveGTCELimit(UXVSuperconductorBase, new WireProperties(GAValues.V[GAValues.UXV], 4, 2));
        addCableAboveGTCELimit(TungstenTitaniumCarbide, new WireProperties(GAValues.V[GAValues.UHV], 4, 16));
        addCableAboveGTCELimit(AbyssalAlloy, new WireProperties(GAValues.V[GAValues.UHV], 2, 8));
        addCableAboveGTCELimit(EnrichedNaquadahAlloy, new WireProperties(GAValues.V[GAValues.UHV], 1, 4));
        addCableAboveGTCELimit(Pikyonium, new WireProperties(GAValues.V[GAValues.UEV], 4, 32));
        addCableAboveGTCELimit(TitanSteel, new WireProperties(GAValues.V[GAValues.UEV], 2, 16));
        addCableAboveGTCELimit(Cinobite, new WireProperties(GAValues.V[GAValues.UIV], 4, 64));
        addCableAboveGTCELimit(BlackTitanium, new WireProperties(GAValues.V[GAValues.UIV], 2, 32));
        addCableAboveGTCELimit(Neutronium, new WireProperties(GAValues.V[GAValues.UMV], 2, 32));
        addCableAboveGTCELimit(UHVSuperconductor, new WireProperties(GAValues.V[GAValues.UHV], 4, 0));
        addCableAboveGTCELimit(UEVSuperconductor, new WireProperties(GAValues.V[GAValues.UEV], 4, 0));
        addCableAboveGTCELimit(UIVSuperconductor, new WireProperties(GAValues.V[GAValues.UIV], 4, 0));
        addCableAboveGTCELimit(UMVSuperconductor, new WireProperties(GAValues.V[GAValues.UMV], 4, 0));
        addCableAboveGTCELimit(UXVSuperconductor, new WireProperties(GAValues.V[GAValues.UXV], 4, 0));
        */

        // Disable Mixer Flag
        YttriumBariumCuprate.addFlags(DISABLE_AUTOGENERATED_MIXER_RECIPE);
        GalliumArsenide.addFlags(DISABLE_AUTOGENERATED_MIXER_RECIPE);
        Polyethylene.addFlags(DISABLE_AUTOGENERATED_MIXER_RECIPE);
        Polytetrafluoroethylene.addFlags(DISABLE_AUTOGENERATED_MIXER_RECIPE);
        TungstenCarbide.addFlags(DISABLE_AUTOGENERATED_MIXER_RECIPE);

        // Plasmas
        Radon.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Carbon.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Magnesium.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Silicon.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Sulfur.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Calcium.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Titanium.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Neon.setProperty(PropertyKey.PLASMA, new PlasmaProperty());
        Potassium.setProperty(PropertyKey.PLASMA, new PlasmaProperty());

        // Decomposition
        Barite.addFlags(DISABLE_DECOMPOSITION);
        TungstenCarbide.addFlags(DISABLE_DECOMPOSITION);
        Uraninite.addFlags(DISABLE_DECOMPOSITION);
        NaquadahAlloy.addFlags(DISABLE_DECOMPOSITION);
        Trona.addFlags(DISABLE_DECOMPOSITION);
        Columbite.addFlags(DISABLE_DECOMPOSITION);
        Tantalite.addFlags(DISABLE_DECOMPOSITION);
        Scheelite.addFlags(DISABLE_DECOMPOSITION);
        Tungstate.addFlags(DISABLE_DECOMPOSITION);

        Spessartine.addFlags(GENERATE_LENS);

        //todo
        // Fine Wires
//        Plutonium.addFlag(GENERATE_FINE_WIRE);

        // Long Rods
//        Uranium.addFlag(GENERATE_LONG_ROD);

        // Core Metal
        Tritanium.addFlags(GA_CORE_METAL.toArray(new MaterialFlag[0]));
        Duranium.addFlags(GA_CORE_METAL.toArray(new MaterialFlag[0]));

        // Replication
        Naquadria.addFlags(DISABLE_REPLICATION);
        Naquadah.addFlags(DISABLE_REPLICATION);
        NaquadahEnriched.addFlags(DISABLE_REPLICATION);

        // Prefix Ignores
        OrePrefix.block.setIgnored(Pyrotheum);
        OrePrefix.block.setIgnored(Cryotheum);

        // Fluids
        Arsenic.setProperty(PropertyKey.FLUID, new FluidProperty());

        // Frames
        Naquadria.addFlags(GENERATE_FRAME);
        BlackSteel.addFlags(GENERATE_FRAME);
        Seaborgium.addFlags(GENERATE_FRAME);
        Bohrium.addFlags(GENERATE_FRAME);
        HSSS.addFlags(GENERATE_FRAME);
    }

    // TODO Clean this up, migrate what we can up to CEu
    private static void initOres() {

        // Ores
        Tellurium.setProperty(PropertyKey.ORE, new OreProperty());
        Pollucite.setProperty(PropertyKey.ORE, new OreProperty());
        Andradite.setProperty(PropertyKey.ORE, new OreProperty());
        Vermiculite.setProperty(PropertyKey.ORE, new OreProperty());
        Alunite.setProperty(PropertyKey.ORE, new OreProperty());
        GlauconiteSand.setProperty(PropertyKey.ORE, new OreProperty());
        Niter.setProperty(PropertyKey.ORE, new OreProperty());


        // Ore Multiplier
        PlatinumMetallicPowder.getProperty(PropertyKey.ORE).setOreMultiplier(2);
        PalladiumMetallicPowder.getProperty(PropertyKey.ORE).setOreMultiplier(2);
        Lepidolite.getProperty(PropertyKey.ORE).setOreMultiplier(2);
        Barytocalcite.getProperty(PropertyKey.ORE).setOreMultiplier(2);

        // Washed In
        PlatinumMetallicPowder.getProperty(PropertyKey.ORE).setWashedIn(SodiumPersulfate);
        RarestMetalResidue.getProperty(PropertyKey.ORE).setWashedIn(SodiumPersulfate);
        IrMetalResidue.getProperty(PropertyKey.ORE).setWashedIn(SodiumPersulfate);
        Witherite.getProperty(PropertyKey.ORE).setWashedIn(SodiumPersulfate);

        // Direct Smelting
        OreProperty property = Cooperite.getProperty(PropertyKey.ORE);
        property.setDirectSmeltResult(null);

        // Byproducts
        property = Zirkelite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Thorium, Zirconium, Cerium);

        property = Caliche.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Niter, Saltpeter, Lepidolite);

        property = Zircon.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Cobalt, Lead, Uranium238);

        property = Pyrochlore.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Apatite, Calcite, Niobium);

        property = Uranium238.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Lead, Uranium238, Thorium);

        property = Alunite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Zinc, Gallium, Iron);

        property = Fluorite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Calcium);

        property = FluoroApatite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Apatite, Fluorite, Phosphorus);

        property = Rhodochrosite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Calcium, Manganese);

        property = Columbite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Iron, Manganese, Niobium);

        property = Barytocalcite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Calcite, Magnesium, Barite);

        property = Witherite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Calcite, Barite, Lead);

        property = Arsenopyrite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Iron, Cobaltite, Cobalt);

        property = Gallite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Sulfur, Copper, Copper, Gallium);

        property = Celestine.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Gypsum, Calcite, Calcite, Strontium);

        property = Bowieite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Nickel, PlatinumMetallicPowder, RarestMetalResidue, CrudeRhodiumMetal);

        property = EnrichedNaquadricCompound.getProperty(PropertyKey.ORE);
        property.setOreByProducts(NaquadricCompound, NaquadriaticCompound);

        property = NaquadricCompound.getProperty(PropertyKey.ORE);
        property.setOreByProducts(EnrichedNaquadricCompound);

        property = PlatinumMetallicPowder.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Nickel, IrMetalResidue, RarestMetalResidue, PlatinumMetallicPowder);

        property = RarestMetalResidue.getProperty(PropertyKey.ORE);
        property.setOreByProducts(IrMetalResidue, IrMetalResidue, IrMetalResidue, RarestMetalResidue);

        property = IrMetalResidue.getProperty(PropertyKey.ORE);
        property.setOreByProducts(PlatinumMetallicPowder, RarestMetalResidue);

        // Byproduct Overrides
        property = Mica.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Apatite, Fluorite, Phosphorus);

        property = Bornite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Pyrite, Cobalt, Cadmium, Gold);

        property = Chalcopyrite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Pyrite, Cobalt, Cadmium, Gold);

        property = Copper.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Cobalt, Gold, Nickel);

        property = Magnetite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Iron, Gold);

        property = Pitchblende.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Thorium, Uranium238, Lead); //todo nuclear rework

        property = Nickel.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Cobalt, PlatinumMetallicPowder, Iron);

        property = Iridium.getProperty(PropertyKey.ORE);
        property.setOreByProducts(PlatinumMetallicPowder, RarestMetalResidue);

        property = Platinum.getProperty(PropertyKey.ORE);
        property.setOreByProducts(Nickel, IrMetalResidue);

        property = Osmium.getProperty(PropertyKey.ORE);
        property.setOreByProducts(IrMetalResidue);

        property = Cooperite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(PalladiumMetallicPowder, Nickel, IrMetalResidue);

        property = Triniite.getProperty(PropertyKey.ORE);
        property.setOreByProducts(NaquadricCompound, Bismuth, Thallium);
    }

    private static void initNuclearMaterial() {
        //todo
//        ThoriumRadioactive.complexity = 100;
//        Protactinium.complexity = 100;
//        UraniumRadioactive.complexity = 100;
//        Neptunium.complexity = 115;
//        PlutoniumRadioactive.complexity = 120;
//        AmericiumRadioactive.complexity = 135;
//        Curium.complexity = 145;
//        Berkelium.complexity = 150;
//        Californium.complexity = 160;
//        Einsteinium.complexity = 170;
//        Fermium.complexity = 185;
//        Mendelevium.complexity = 200;
//
//        Thorium.addFlag(GENERATE_LONG_ROD);
//        Uranium235.addFlag(GENERATE_LONG_ROD);
//
//        Thorium232.fertile = true;
//        Thorium232.isotopeDecay.put(Thorium233, 100);
//        Thorium232.isotopeDecay.put(Protactinium233, 1000);
//        Thorium232.isotopeDecay.put(Uranium233, 8900);
//
//        Thorium233.isotopeDecay.put(Protactinium233, 9000);
//
//        Protactinium233.isotopeDecay.put(Uranium233, 9000);
//
//        //Uranium
//        UraniumRadioactive.composition.put(Uranium238Isotope, 9890);
//        UraniumRadioactive.composition.put(Uranium235Isotope, 100);
//        UraniumRadioactive.composition.put(Uranium234, 10);
//
//        Uranium233.fissile = true;
//        Uranium235Isotope.fissile = true;
//        Uranium234.fertile = true;
//        Uranium238Isotope.fertile = true;
//
//        Uranium235Isotope.baseHeat = 10;
//        Uranium233.baseHeat = 7;
//
//        Uranium234.isotopeDecay.put(Uranium235Isotope, 9000);
//        Uranium238Isotope.isotopeDecay.put(Uranium239, 100);
//        Uranium238Isotope.isotopeDecay.put(Neptunium239, 1000);
//        Uranium238Isotope.isotopeDecay.put(Plutonium239, 8900);
//        Uranium239.isotopeDecay.put(Neptunium239, 9000);
//
//
//        //neptunium
//        Neptunium.composition.put(Neptunium235, 2000);
//        Neptunium.composition.put(Neptunium237, 5000);
//        Neptunium.composition.put(Neptunium239, 3000);
//
//        Neptunium237.fissile = true;
//        Neptunium237.baseHeat = 11;
//
//        Neptunium237.isotopeDecay.put(Protactinium233, 9000);
//        Neptunium239.isotopeDecay.put(Plutonium239, 9000);
//        Neptunium235.isotopeDecay.put(Uranium235Isotope, 9000);
//
//        //plutonium
//        PlutoniumRadioactive.composition.put(Plutonium244, 9890);
//        PlutoniumRadioactive.composition.put(Plutonium241Isotope, 100);
//        PlutoniumRadioactive.composition.put(Plutonium240, 10);
//
//        Plutonium241Isotope.fissile = true;
//        Plutonium239.fissile = true;
//        Plutonium240.fertile = true;
//        Plutonium244.fertile = true;
//
//        Plutonium241Isotope.baseHeat = 13;
//        Plutonium239.baseHeat = 10;
//
//        Plutonium240.isotopeDecay.put(Plutonium241Isotope, 9000);
//        Plutonium244.isotopeDecay.put(Plutonium245, 100);
//        Plutonium244.isotopeDecay.put(Americium245, 1000);
//        Plutonium244.isotopeDecay.put(Curium245, 8900);
//        Plutonium245.isotopeDecay.put(Americium245, 9000);
//
//        //Americium
//        AmericiumRadioactive.composition.put(Americium241, 2000);
//        AmericiumRadioactive.composition.put(Americium243, 5000);
//        AmericiumRadioactive.composition.put(Americium245, 3000);
//
//        Americium243.fissile = true;
//        Americium243.baseHeat = 14;
//
//        Americium243.isotopeDecay.put(Neptunium239, 9000);
//        Americium245.isotopeDecay.put(Curium245, 9000);
//        Americium241.isotopeDecay.put(Plutonium241Isotope, 9000);
//
//        //Curium
//        Curium.composition.put(Curium250, 9890);
//        Curium.composition.put(Curium247, 100);
//        Curium.composition.put(Curium246, 10);
//
//        Curium245.fissile = true;
//        Curium247.fissile = true;
//        Curium246.fertile = true;
//        Curium250.fertile = true;
//
//        Curium245.baseHeat = 13;
//        Curium247.baseHeat = 16;
//
//        Curium246.isotopeDecay.put(Curium247, 9000);
//        Curium250.isotopeDecay.put(Curium251, 100);
//        Curium250.isotopeDecay.put(Berkelium251, 1000);
//        Curium250.isotopeDecay.put(Californium251, 8900);
//        Curium251.isotopeDecay.put(Americium245, 9000);
//
//        //Berkelium
//        Berkelium.composition.put(Berkelium247, 2000);
//        Berkelium.composition.put(Berkelium249, 5000);
//        Berkelium.composition.put(Berkelium251, 3000);
//
//        Berkelium249.fissile = true;
//        Berkelium249.baseHeat = 17;
//
//        Berkelium249.isotopeDecay.put(Americium245, 9000);
//        Berkelium251.isotopeDecay.put(Californium251, 9000);
//        Berkelium247.isotopeDecay.put(Curium247, 9000);
//
//        //Californium
//        Californium.composition.put(Californium252, 9890);
//        Californium.composition.put(Californium253, 100);
//        Californium.composition.put(Californium256, 10);
//
//        Californium251.fissile = true;
//        Californium253.fissile = true;
//        Californium252.fertile = true;
//        Californium256.fertile = true;
//
//        Californium251.baseHeat = 16;
//        Californium253.baseHeat = 19;
//
//        Californium252.isotopeDecay.put(Californium253, 9000);
//        Californium256.isotopeDecay.put(Californium257, 100);
//        Californium256.isotopeDecay.put(Einsteinium257, 1000);
//        Californium256.isotopeDecay.put(Fermium257, 8900);
//        Californium257.isotopeDecay.put(Einsteinium257, 9000);
//
//        //Einsteinium
//        Einsteinium.composition.put(Einsteinium253, 2000);
//        Einsteinium.composition.put(Einsteinium255, 5000);
//        Einsteinium.composition.put(Einsteinium257, 3000);
//
//        Einsteinium255.fissile = true;
//        Einsteinium255.baseHeat = 20;
//
//        Einsteinium255.isotopeDecay.put(Berkelium251, 9000);
//        Einsteinium257.isotopeDecay.put(Fermium257, 9000);
//        Einsteinium253.isotopeDecay.put(Californium253, 9000);
//
//
//        //Fermium
//        Fermium.composition.put(Fermium258, 9890);
//        Fermium.composition.put(Fermium259, 100);
//        Fermium.composition.put(Fermium262, 10);
//
//        Fermium257.fissile = true;
//        Fermium259.fissile = true;
//        Fermium258.fertile = true;
//        Fermium262.fertile = true;
//
//        Fermium257.baseHeat = 19;
//        Fermium259.baseHeat = 22;
//
//        Fermium258.isotopeDecay.put(Fermium259, 9000);
//        Fermium262.isotopeDecay.put(Fermium263, 1000);
//        Fermium262.isotopeDecay.put(Mendelevium263, 9000);
//        Fermium263.isotopeDecay.put(Mendelevium263, 9000);
//
//        //Mendelevium
//        Mendelevium.composition.put(Mendelevium259, 2000);
//        Mendelevium.composition.put(Mendelevium261, 5000);
//        Mendelevium.composition.put(Mendelevium263, 3000);
//
//        Mendelevium261.fissile = true;
//        Mendelevium261.baseHeat = 23;
//        Mendelevium261.isotopeDecay.put(Einsteinium257, 9000);
//        Mendelevium259.isotopeDecay.put(Fermium259, 9000);

    }
}
