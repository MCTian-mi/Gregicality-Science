package gregicality.science.api.capability.impl;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GasMap extends Object2DoubleArrayMap<Fluid> implements INBTSerializable<NBTTagCompound> {

    public static final GasMap EMPTY = new GasMap();

    /**
     * Minimum ratio of a gas exists in this map. If smaller, that type of gas is removed.
     */
    public static final double MIN_RATIO = 0.000_000_1;
    private double totalGas;

    public GasMap() {
        super();
        this.totalGas = 0;
    }

    public void merge(GasMap other) {
        for (Object2DoubleMap.Entry<Fluid> entry : other.object2DoubleEntrySet()) {
            pushGas(entry.getKey(), entry.getDoubleValue());
            totalGas += entry.getDoubleValue();
        }
    }

    public void popGas(double amount) {
        double total = getTotalGasAmount();
        if (amount < 0d || amount > totalGas) return;
        for (Fluid fluid : this.keySet()) {
            popGas(fluid, amount * getRatio(fluid, total));
        }
    }

    public void popGas(Fluid fluid, double amount) {
        if (amount < 0d || amount > getGasAmount(fluid)) return;
        this.put(fluid, getGasAmount(fluid) - amount);
        this.totalGas -= amount;
    }

    public void pushGas(Fluid fluid, double amount) {
        if (amount < 0d) throw new IllegalArgumentException("Pushed gas amount must be positive!");
        this.put(fluid, getGasAmount(fluid) + amount);
        this.totalGas += amount;
    }

    public void setGasAmount(Fluid fluid, double amount) {
        if (amount < 0d) throw new IllegalArgumentException("Set amount must be positive!");
        this.totalGas -= this.getDouble(fluid);
        this.put(fluid, amount);
        this.totalGas += amount;
    }

    public double getGasAmount(Fluid fluid) {
        return this.getDouble(fluid);
    }

    public double getTotalGasAmount() {
        return totalGas;
    }

    public double getRatio(Fluid fluid) {
        return getRatio(fluid, this.totalGas);
    }

    public double getRatio(Fluid fluid, double total) {
        return this.getDouble(fluid) / total;
    }

    public void cleanUp() {
        // Never modify a map while iterating it
        for (Fluid fluid : this.keySet().toArray(new Fluid[0])) {
            if (getRatio(fluid) < MIN_RATIO) {
                this.totalGas -= this.removeDouble(fluid);
            }
        }
    }

    public double recalculateTotalGas() {
        cleanUp();
        this.totalGas = 0;
        for (double amount : this.values()) {
            this.totalGas += amount;
        }
        return this.totalGas;
    }

    @Override
    public void clear() {
        super.clear();
        this.totalGas = 0;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setDouble("TotalGas", this.totalGas);
        NBTTagList nbtTagList = new NBTTagList();
        cleanUp(); // TODO: better not use it here
        for (Object2DoubleMap.Entry<Fluid> entry : this.object2DoubleEntrySet()) {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            fluidCompound.setString("GasName", FluidRegistry.getFluidName(entry.getKey()));
            fluidCompound.setDouble("Amount", entry.getDoubleValue());
            nbtTagList.appendTag(fluidCompound);
        }
        compound.setTag("Gases", nbtTagList);
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (nbt == null) return;
        if (nbt.hasKey("Gases", Constants.NBT.TAG_LIST)) {
            NBTTagList nbtTagList = nbt.getTagList("Gases", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                NBTTagCompound fluidCompound = nbtTagList.getCompoundTagAt(i);
                this.put(FluidRegistry.getFluid(fluidCompound.getString("GasName")), fluidCompound.getDouble("Amount"));
            }
            this.totalGas = nbt.hasKey("TotalGas", Constants.NBT.TAG_DOUBLE) ? nbt.getDouble("TotalGas") : recalculateTotalGas();
        }
    }
}
