package gregicality.science.api.capability.impl;

import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.recipes.recipeproperties.PressureRequirementPropertyList;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.RecipeMap;
import net.minecraft.nbt.NBTTagCompound;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SimplePressureRecipeLogic extends RecipeLogicEnergy implements IRecipeLogicPressure {

    protected final Supplier<IPressureContainer> pressureContainer;
    private PressureRequirementPropertyList pressureRequirements = PressureRequirementPropertyList.EMPTY_LIST;

    public SimplePressureRecipeLogic(MetaTileEntity tileEntity, RecipeMap<?> recipeMap, Supplier<IEnergyContainer> energyContainer, Supplier<IPressureContainer> pressureContainer) {
        super(tileEntity, recipeMap, energyContainer);
        this.pressureContainer = pressureContainer;
    }

    @Override
    public IPressureContainer getPressureContainer() {
        return pressureContainer.get();
    }

    @Override
    protected boolean canProgressRecipe() {
        if (getPreviousRecipe() == null) return true;
        return super.canProgressRecipe() && checkPressureRequirements(getPreviousRecipe());
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        return super.checkRecipe(recipe) && checkPressureRequirements(recipe);
    }

    @Override
    protected void setupRecipe(Recipe recipe) {
        super.setupRecipe(recipe);
        if (hasPressureRequirements(recipe)) {
            this.pressureRequirements = getPressureRequirements(recipe);
        }
    }

    @Override
    public @NotNull NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        if (this.progressTime > 0) {
            compound.setTag("PressureRequirements", pressureRequirements.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        super.deserializeNBT(compound);
        if (this.progressTime > 0) {
            this.pressureRequirements.deserializeNBT(compound.getCompoundTag("pressure"));
        } else {
            this.pressureRequirements = PressureRequirementPropertyList.EMPTY_LIST;
        }
    }
}
