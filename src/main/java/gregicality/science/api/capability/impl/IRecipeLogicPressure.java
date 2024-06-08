package gregicality.science.api.capability.impl;

import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementProperty;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementPropertyList;
import gregtech.api.recipes.Recipe;

import javax.annotation.Nonnull;

public interface IRecipeLogicPressure {

    IPressureContainer getPressureContainer();

    default boolean checkPressureRequirements(@Nonnull Recipe recipe) {
        return getPressureRequirements(recipe).test(getPressureContainer());
    }

    default PressureRequirementPropertyList getPressureRequirements(Recipe recipe) {
        return recipe.getProperty(PressureRequirementProperty.getInstance(), PressureRequirementPropertyList.EMPTY_LIST);
    }

    default boolean hasPressureRequirements(Recipe recipe) {
        return recipe.getRecipePropertyStorage() != null && recipe.hasProperty(PressureRequirementProperty.getInstance());
    }
}
