package gregicality.science.api.recipes.recipeproperties;

import gregicality.science.api.utils.NumberFormattingUtil;
import gregicality.science.client.render.GCYSGuiTextures;
import gregtech.api.recipes.recipeproperties.RecipeProperty;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PressureRequirementProperty extends RecipeProperty<PressureRequirementPropertyList> {

    public static final String KEY = "pressure_requirement";
    private static final Position PRESSURE_INDICATOR_POSITION = new Position(79, 42);
    private static final Size PRESSURE_INDICATOR_SIZE = new Size(18, 18);
    private static PressureRequirementProperty INSTANCE;

    private PressureRequirementProperty() {
        super(KEY, PressureRequirementPropertyList.class);
    }

    public static PressureRequirementProperty getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PressureRequirementProperty();
        }
        return INSTANCE;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getTooltipStrings(List<String> tooltip, int mouseX, int mouseY, Object value) {
        super.getTooltipStrings(tooltip, mouseX, mouseY, value);

        if (mouseX < PRESSURE_INDICATOR_POSITION.getX() || mouseX > PRESSURE_INDICATOR_POSITION.getX() + PRESSURE_INDICATOR_SIZE.getWidth() ||
                mouseY < PRESSURE_INDICATOR_POSITION.getY() || mouseY > PRESSURE_INDICATOR_POSITION.getY() + PRESSURE_INDICATOR_SIZE.getHeight())
            return;

        PressureRequirementPropertyList requirementList = (PressureRequirementPropertyList) value;
        List<String> pressureTooltips = new ArrayList<>(); // TODO: better formatting
        if (requirementList.isHasTotalPressureRequirement()) {
            pressureTooltips.add(PressureRequirementType.WITHIN_RANGE.format(
                    NumberFormattingUtil.formatDoubleToCompactString(requirementList.getMinTotalPressure()),
                    NumberFormattingUtil.formatDoubleToCompactString(requirementList.getMaxTotalPressure())));
        }
        for (Fluid gas : requirementList.getRequirementGases()) {
            pressureTooltips.add(PressureRequirementType.WITHIN_RANGE.format(
                    gas,
                    NumberFormattingUtil.formatDoubleToCompactString(requirementList.getMinPressureMap().getDouble(gas)),
                    NumberFormattingUtil.formatDoubleToCompactString(requirementList.getMaxPressureMap().getDouble(gas))));
        }
        tooltip.addAll(pressureTooltips);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int x, int y, int color, Object value) {
        GlStateManager.enableLighting();
        GlStateManager.enableLight(1);
        GCYSGuiTextures.PRESSURE_INDICATOR.draw(PRESSURE_INDICATOR_POSITION.getX(), PRESSURE_INDICATOR_POSITION.getY(), PRESSURE_INDICATOR_SIZE.getWidth(), PRESSURE_INDICATOR_SIZE.getHeight());
        GlStateManager.disableLight(1);
        GlStateManager.disableLighting();
        minecraft.fontRenderer.drawString(I18n.format("gcys.recipe.has_pressure_requirement"), x, y, color);
    }
}
