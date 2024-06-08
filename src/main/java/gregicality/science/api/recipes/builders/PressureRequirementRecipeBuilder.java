package gregicality.science.api.recipes.builders;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementProperty;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeBuilder;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.recipeproperties.RecipePropertyStorage;
import gregtech.api.util.EnumValidationResult;
import gregtech.api.util.GTLog;
import gregtech.api.util.TextFormattingUtil;
import gregtech.api.util.ValidationResult;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
            this.pressure(((Number) value).doubleValue());
            return true;
        }
        return super.applyProperty(key, value);
    }

    @Nonnull
    public PressureRequirementRecipeBuilder pressure(PressureRequirementProperty.PressureRequirement requirement) {
        if (pressure <= 0) {
            GTLog.logger.error("Pressure cannot be less than or equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(PressureProperty.getInstance(), pressure);
        return this;
    }

    @Nonnull
    public PressureRequirementRecipeBuilder pressure(double pressure) {
        if (pressure <= 0) {
            GTLog.logger.error("Pressure cannot be less than or equal to 0", new IllegalArgumentException());
            recipeStatus = EnumValidationResult.INVALID;
        }
        this.applyProperty(PressureProperty.getInstance(), pressure);
        return this;
    }

    @Override
    public ValidationResult<Recipe> build() {
        if (this.recipePropertyStorage == null) this.recipePropertyStorage = new RecipePropertyStorage();
        if (this.recipePropertyStorage.hasRecipeProperty(PressureProperty.getInstance())) {
            if (this.recipePropertyStorage.getRecipePropertyValue(PressureProperty.getInstance(), -1.0D) <= 0) {
                this.recipePropertyStorage.store(PressureProperty.getInstance(), GCYSValues.EARTH_PRESSURE);
            }
        } else {
            this.recipePropertyStorage.store(PressureProperty.getInstance(), GCYSValues.EARTH_PRESSURE);
        }

        return super.build();
    }

    public double getPressure() {
        return this.recipePropertyStorage == null ? 0.0D :
                this.recipePropertyStorage.getRecipePropertyValue(PressureProperty.getInstance(), 0.0D);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append(PressureProperty.getInstance().getKey(), TextFormattingUtil.formatNumbers(getPressure()))
                .toString();
    }
}
