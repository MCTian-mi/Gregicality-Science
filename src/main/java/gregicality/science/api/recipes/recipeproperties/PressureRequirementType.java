package gregicality.science.api.recipes.recipeproperties;

import gregtech.api.util.LocalizationUtils;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public enum PressureRequirementType {

    HIGHER_THAN("gcys.recipe.pressure_requirement.higher_than"),
    LOWER_THAN("gcys.recipe.pressure_requirement.lower_than"),
    WITHIN_RANGE("gcys.recipe.pressure_requirement.within_range"),
    APPROXIMATE("gcys.recipe.pressure_requirement.approximate"),
    NULL("");

    private final String langKey;

    PressureRequirementType(String langKey) {
        this.langKey = langKey;
    }

    public String format(Fluid fluid, String... args) {
        return format(fluid.getLocalizedName(new FluidStack(fluid, 1)), args);
    }

    public String format(String... args) {
        return format(LocalizationUtils.format("gcys.recipe.pressure_requirement.total"), args);
    }

    private String format(String name, String... args) {
        TextComponentTranslation translation = new TextComponentTranslation(langKey, name, args[0], args[1]); // TODO
        return translation.getFormattedText();
    }

}
