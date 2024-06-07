package gregicality.science.api.recipes.recipeproperties;

import gregicality.science.api.utils.NumberFormattingUtil;
import gregtech.api.recipes.recipeproperties.RecipeProperty;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PressureRequirementProperty extends RecipeProperty<PressureRequirementProperty.PressureRequirement> {

    public static final String KEY = "pressure_requirement";

    private static PressureRequirementProperty INSTANCE;

    private PressureRequirementProperty() {
        super(KEY, PressureRequirement.class);
    }

    public static PressureRequirementProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PressureRequirementProperty();
        }
        return INSTANCE;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int x, int y, int color, Object value) {
        minecraft.fontRenderer.drawString(((PressureRequirement) value).getRequirementText(), x, y, color);
    }

    public class PressureRequirement {

        @Getter
        private final PressureRequirementType type;
        @Getter
        private final double maxP;
        @Getter
        private final double minP;
        @Nullable
        @Getter
        private final Fluid gas;
        @Getter
        private final String requirementText;

        public PressureRequirement(PressureRequirementType type, @Nullable Fluid gas, int... args) {
            this.gas = gas;
            this.type = type;
            String gasName = gas == null ? "" : gas.getLocalizedName(new FluidStack(gas, 1));
            switch (type) {
                case HIGHER_THAN:
                    this.minP = args[0];
                    this.maxP = Double.MAX_VALUE;
                    break;
                case LOWER_THAN:
                    this.minP = Double.MIN_VALUE;
                    this.maxP = args[0];
                    break;
                case WITHIN_RANGE:
                    this.minP = args[0];
                    this.maxP = args[1];
                    break;
                case APPROXIMATE:
                    this.minP = args[0] - args[1];
                    this.maxP = args[0] + args[1];
                    break;
                default:
                    this.minP = Double.MIN_VALUE;
                    this.maxP = Double.MAX_VALUE;
                    break;
            }
            this.requirementText = I18n.format(
                    "gcys.recipe.pressure_requirement",
                    gasName,
                    NumberFormattingUtil.formatDoubleToCompactString(this.minP),
                    NumberFormattingUtil.formatDoubleToCompactString(this.maxP));
        }
    }
}
