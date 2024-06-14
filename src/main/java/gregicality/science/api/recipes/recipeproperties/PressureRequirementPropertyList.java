package gregicality.science.api.recipes.recipeproperties;

import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.impl.GasMap;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

@Getter
public class PressureRequirementPropertyList implements INBTSerializable<NBTTagCompound> {

    public static final PressureRequirementPropertyList EMPTY_LIST = new PressureRequirementPropertyList();

    private final ArrayList<Fluid> requirementGases;
    private final GasMap maxPressureMap;
    private final GasMap minPressureMap;
    private boolean hasTotalPressureRequirement;
    private double maxTotalPressure;
    private double minTotalPressure;

    public PressureRequirementPropertyList() {
        this.hasTotalPressureRequirement = false;
        this.requirementGases = new ArrayList<>();
        this.maxPressureMap = new GasMap();
        this.minPressureMap = new GasMap();
    }

    public void addRequirement(PressureRequirementType type, @Nullable Fluid gas, double[] args) {
        double minP, maxP;
        switch (type) {
            case HIGHER_THAN:
                minP = args[0];
                maxP = Double.MAX_VALUE;
                break;
            case LOWER_THAN:
                minP = 0;
                maxP = args[0];
                break;
            case WITHIN_RANGE:
                minP = args[0];
                maxP = args[1];
                break;
            case APPROXIMATE:
                double radius = args.length >= 2 ? args[1] : 0.1D * args[0];
                minP = args[0] - radius;
                maxP = args[0] + radius;
                break;
            default:
                minP = Double.MIN_VALUE;
                maxP = Double.MAX_VALUE;
                break;
        }

        if (gas == null) {
            this.hasTotalPressureRequirement = true;
            this.maxTotalPressure = maxP;
            this.minTotalPressure = minP;
        } else {
            this.requirementGases.add(gas);
            this.maxPressureMap.put(gas, maxP);
            this.minPressureMap.put(gas, minP);
        }
    }

    public boolean test(IPressureContainer pressureContainer) {
        if (hasTotalPressureRequirement) {
            if (pressureContainer.getPressure() > maxTotalPressure || pressureContainer.getPressure() < minTotalPressure) {
                return false;
            }
        }
        for (Fluid gas : requirementGases) {
            double pressure = pressureContainer.getPressure(gas);
            if (pressure > maxPressureMap.getDouble(gas) || pressure < minPressureMap.getDouble(gas)) return false;
        }
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setBoolean("hasTotalPressureRequirement", hasTotalPressureRequirement);
        nbt.setDouble("minTotalPressure", minTotalPressure);
        nbt.setDouble("maxTotalPressure", maxTotalPressure);
        NBTTagList gasList = new NBTTagList();
        for (Fluid gas : requirementGases) {
            NBTTagCompound gasTag = new NBTTagCompound();
            gasTag.setString("gas", FluidRegistry.getFluidName(gas));
            gasList.appendTag(gasTag);
        }
        nbt.setTag("gases", gasList);
        nbt.setTag("minPressureMap", minPressureMap.serializeNBT());
        nbt.setTag("maxPressureMap", maxPressureMap.serializeNBT());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.hasTotalPressureRequirement = nbt.getBoolean("hasTotalPressureRequirement");
        this.minTotalPressure = nbt.getDouble("minTotalPressure");
        this.maxTotalPressure = nbt.getDouble("maxTotalPressure");
        NBTTagList gasList = nbt.getTagList("gases", Constants.NBT.TAG_LIST);
        for (int i = 0; i < gasList.tagCount(); i++) {
            Fluid gas = FluidRegistry.getFluid(gasList.getCompoundTagAt(i).getString("gas"));
            this.requirementGases.add(gas);
        }
        this.minPressureMap.deserializeNBT(nbt.getCompoundTag("minPressureMap"));
        this.maxPressureMap.deserializeNBT(nbt.getCompoundTag("maxPressureMap"));
    }
}
