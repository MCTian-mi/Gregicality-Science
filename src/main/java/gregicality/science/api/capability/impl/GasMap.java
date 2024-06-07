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

    private static final double MIN_RATIO = 0.0001;
    private double totalGas;

    public GasMap() {
        super();
        this.totalGas = 0;
    }

    public void merge(GasMap other) {
        for (Object2DoubleMap.Entry<Fluid> entry : other.object2DoubleEntrySet()) {
            pushGas(entry.getKey(), getGasAmount(entry.getKey()) + entry.getDoubleValue());
            totalGas += entry.getDoubleValue();
        }
    }

    public void popGas(double amount) {
        if (amount < 0d || amount > this.totalGas) return;
        for (Fluid fluid : this.keySet()) {
            popGas(fluid, amount * getRatio(fluid));
        }
    }

    public void popGas(Fluid fluid, double amount) {
        if (amount < 0d || amount > getGasAmount(fluid)) return;
        setGasAmount(fluid, getGasAmount(fluid) - amount);
    }

    public void pushGas(Fluid fluid, double amount) {
        if (amount < 0d) throw new IllegalArgumentException("Pushed gas amount must be positive!");
        setGasAmount(fluid, getGasAmount(fluid) + amount);
    }

    public void setGasAmount(Fluid fluid, double amount) {
        if (amount < 0d) throw new IllegalArgumentException("Set amount must be positive!");
        this.totalGas -= this.getOrDefault(fluid, 0d);
        this.put(fluid, amount);
        this.totalGas += amount;
    }

    public double getGasAmount(Fluid fluid) {
        return this.getOrDefault(fluid, 0d);
    }

    public double getTotalGasAmount() {
        return totalGas;
    }

    public double getRatio(Fluid fluid) {
        return this.getDouble(fluid) / this.totalGas;
    }

    public void cleanUp() {
        for (Fluid fluid : this.keySet()) {
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
