package gregicality.science.loaders.recipe.test;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementType;

import static gregicality.science.api.recipes.GCYSRecipeMaps.GCYS_AUTOCLAVE_RECIPES;
import static gregtech.api.GTValues.HV;
import static gregtech.api.GTValues.VA;
import static gregtech.api.unification.material.Materials.*;

public class GCYSAutoclaveRecipes {

    public static void init() {

        // GCYS Autoclave Recipes
        GCYS_AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(Hydrogen.getFluid(6000), Benzene.getFluid(1000))
                .fluidOutputs(Cyclohexane.getFluid(1000))
                .pressure(PressureRequirementType.HIGHER_THAN, Hydrogen.getFluid(), GCYSValues.EARTH_PRESSURE * 10)
                .duration(400)
                .EUt(VA[HV])
                .buildAndRegister();

        GCYS_AUTOCLAVE_RECIPES.recipeBuilder()
                .fluidInputs(Propane.getFluid(1000))
                .fluidOutputs(Propene.getFluid(1000), Hydrogen.getFluid(2000))
                .pressure(PressureRequirementType.LOWER_THAN, Hydrogen.getFluid(), GCYSValues.EARTH_PRESSURE * 0.01)
                .pressure(PressureRequirementType.LOWER_THAN, GCYSValues.EARTH_PRESSURE * 0.1)
                .duration(400)
                .EUt(VA[HV])
                .buildAndRegister();
    }
}
