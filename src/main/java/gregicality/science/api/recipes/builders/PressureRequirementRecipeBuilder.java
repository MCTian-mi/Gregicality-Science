package gregicality.science.api.recipes.builders;

import gregicality.science.api.recipes.recipeproperties.PressureRequirementProperty;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementPropertyList;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementType;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.recipeproperties.RecipePropertyStorage;
import gregtech.api.util.ValidationResult;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class PressureRequirementRecipeBuilder extends RecipeBuilder<PressureRequirementRecipeBuilder> {

    @SuppressWarnings("unused")
    public PressureRequirementRecipeBuilder() {
    }

    @SuppressWarnings("unused")
    public PressureRequirementRecipeBuilder(Recipe recipe, RecipeMap<PressureRequirementRecipeBuilder> recipeMap) {
        super(recipe, recipeMap);
    }

    public PressureRequirementRecipeBuilder(PressureRequirementRecipeBuilder builder) {
        super(builder);
    }

    @Override
    public PressureRequirementRecipeBuilder copy() {
        return new PressureRequirementRecipeBuilder(this);
    }

    @Override
    public boolean applyProperty(@Nonnull String key, Object value) {
        if (key.equals(PressureRequirementProperty.KEY)) {
            this.pressure((PressureRequirementPropertyList) value);
            return true;
        }
        return super.applyProperty(key, value);
    }

    @Nonnull
    public PressureRequirementRecipeBuilder pressure(PressureRequirementPropertyList pressureRequirements) {
        this.applyProperty(PressureRequirementProperty.getInstance(), pressureRequirements);
        return this;
    }

    @Nonnull
    public PressureRequirementRecipeBuilder pressure(PressureRequirementType type, Fluid fluid, double... args) {
        PressureRequirementPropertyList pressureRequirements = getPressureRequirements();
        if (pressureRequirements == PressureRequirementPropertyList.EMPTY_LIST) {
            pressureRequirements = new PressureRequirementPropertyList();
            this.applyProperty(PressureRequirementProperty.getInstance(), pressureRequirements);
        }
        pressureRequirements.addRequirement(type, fluid, args);
        return this;
    }

    @Nonnull
    public PressureRequirementRecipeBuilder pressure(PressureRequirementType type, double... args) {
        pressure(type, null, args);
        return this;
    }

    public PressureRequirementPropertyList getPressureRequirements() {
        return this.recipePropertyStorage == null ? PressureRequirementPropertyList.EMPTY_LIST :
                this.recipePropertyStorage.getRecipePropertyValue(PressureRequirementProperty.getInstance(),
                        PressureRequirementPropertyList.EMPTY_LIST);
    }

    @Override
    public ValidationResult<Recipe> build() { // TODO: do we really need this?
        if (this.recipePropertyStorage == null) this.recipePropertyStorage = new RecipePropertyStorage();
        if (!this.recipePropertyStorage.hasRecipeProperty(PressureRequirementProperty.getInstance())) {
            this.recipePropertyStorage.store(PressureRequirementProperty.getInstance(), PressureRequirementPropertyList.EMPTY_LIST);
        }
        return super.build();
    }
}
