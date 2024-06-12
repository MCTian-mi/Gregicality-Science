package gregicality.science.api.unification.materials.properties;


import gregicality.science.api.GCYSValues;
import gregtech.api.GTValues;
import gregtech.api.fluids.FluidBuilder;
import gregtech.api.fluids.store.FluidStorageKeys;
import gregtech.api.unification.material.info.MaterialIconSet;
import gregtech.api.unification.material.properties.*;

import static gregicality.science.api.GCYSValues.*;
import static gregicality.science.api.unification.materials.GCYSMaterials.GalvanizedSteel;
import static gregtech.api.unification.material.Materials.*;

public class GCYSMaterialPropertyAdditions {

    public static void init() {

        // Dusts
        Praseodymium.setProperty(PropertyKey.DUST, new DustProperty());
        Scandium.setProperty(PropertyKey.DUST, new DustProperty());
        Gadolinium.setProperty(PropertyKey.DUST, new DustProperty());
        Terbium.setProperty(PropertyKey.DUST, new DustProperty());
        Dysprosium.setProperty(PropertyKey.DUST, new DustProperty());
        Holmium.setProperty(PropertyKey.DUST, new DustProperty());
        Erbium.setProperty(PropertyKey.DUST, new DustProperty());
        Thulium.setProperty(PropertyKey.DUST, new DustProperty());
        Ytterbium.setProperty(PropertyKey.DUST, new DustProperty());
        Zirconium.setProperty(PropertyKey.DUST, new DustProperty());
        Tellurium.setProperty(PropertyKey.DUST, new DustProperty());
        Selenium.setProperty(PropertyKey.DUST, new DustProperty());
        Rubidium.setProperty(PropertyKey.DUST, new DustProperty());
        Thallium.setProperty(PropertyKey.DUST, new DustProperty());

        // Ingots
        Germanium.setProperty(PropertyKey.INGOT, new IngotProperty());
        Rhenium.setProperty(PropertyKey.INGOT, new IngotProperty());

        // Blast
        Germanium.setProperty(PropertyKey.BLAST, new BlastProperty(1211, BlastProperty.GasTier.HIGH));
//        Germanium.setProperty(PropertyKey.BLAST, (blastProperty) -> new BlastProperty(1211, BlastProperty.GasTier.HIGH).setEutOverride(GTValues.VA[GTValues.EV])); // TODO

        // Fluids
        Bromine.setProperty(PropertyKey.FLUID, new FluidProperty());
        Bromine.setMaterialIconSet(MaterialIconSet.FLUID);
        Germanium.setProperty(PropertyKey.FLUID, new FluidProperty());

        FluidProperty fluidProperty = new FluidProperty();
        fluidProperty.getStorage().enqueueRegistration(FluidStorageKeys.LIQUID, new FluidBuilder().temperature(332));
        SodiumBisulfate.setProperty(PropertyKey.FLUID, fluidProperty);

        // Ore Byproducts
        // TODO Fix ore byproduct changes
        Pollucite.getProperty(PropertyKey.ORE).setOreByProducts(Aluminium, Potassium, Caesium, Pollucite);

        // Cable Properties
        WireProperties wireProp = RutheniumTriniumAmericiumNeutronate.getProperty(PropertyKey.WIRE);
        wireProp.setSuperconductor(false);
        wireProp.setLossPerBlock(32);
        wireProp.setVoltage((int) GTValues.V[GTValues.UIV]);

        // PressurePipe Properties
        // TODO
        Paper.setProperty(GCYSPropertyKey.PRESSURE_PIPE, new PressurePipeProperties(GCYSValues.P[EAP] * 0.9, GCYSValues.P[EAP] * 1.1, 60, 1f));
        Copper.setProperty(GCYSPropertyKey.PRESSURE_PIPE, new PressurePipeProperties(GCYSValues.P[LV], GCYSValues.P[LP], 120, 1f));
        GalvanizedSteel.setProperty(GCYSPropertyKey.PRESSURE_PIPE, new PressurePipeProperties(GCYSValues.P[MV], GCYSValues.P[MP], 400, 1f));
        BlackSteel.setProperty(GCYSPropertyKey.PRESSURE_PIPE, new PressurePipeProperties(GCYSValues.P[HV], GCYSValues.P[HP], 16000, 1f));
        Neutronium.setProperty(GCYSPropertyKey.PRESSURE_PIPE, new PressurePipeProperties(GCYSValues.P[IVV], GCYSValues.P[NSP], 100000, 1f));
    }
}
