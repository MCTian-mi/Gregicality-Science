package gregicality.science.api.capability.impl;

import codechicken.lib.fluid.FluidUtils;
import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.recipes.FluidKey;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Map;

import static gregtech.api.unification.material.Materials.Air;

public class PressureContainer extends MTETrait implements IPressureContainer {

    private final double minPressure;
    private final double maxPressure;
    private final int volume;

    private Object2DoubleMap<Fluid> particles;

    /**
     * Default pressure container
     * {@link IPressureContainer}
     *
     * @param volume the volume of the container, must be nonzero
     */
    public PressureContainer(MetaTileEntity metaTileEntity, double minPressure, double maxPressure, int volume) {
        super(metaTileEntity);
        this.minPressure = minPressure;
        this.maxPressure = maxPressure;
        this.volume = volume;
        this.particles = new Object2DoubleLinkedOpenHashMap<>();
        this.particles.put(Air.getFluid(), volume * GCYSValues.EARTH_PRESSURE);
    }

    @Override
    public double getMaxPressure() {
        return this.maxPressure;
    }

    @Override
    public Map<Fluid, Double> getParticleMap() {
        return particles;
    }

    @Override
    public int getVolume() {
        return this.volume;
    }

    @Override
    public double getMinPressure() {
        return this.minPressure;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagList nbtTagList = new NBTTagList();
        cleanUp(); // TODO: better not use it here
        for (Object2DoubleMap.Entry<Fluid> entry : this.particles.object2DoubleEntrySet()) {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            fluidCompound.setString("fluid", FluidRegistry.getFluidName(entry.getKey()));
            fluidCompound.setDouble("amount", entry.getDoubleValue());
            nbtTagList.appendTag(fluidCompound);
        }
        compound.setTag("particles", nbtTagList);
        return compound;
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        NBTTagList nbtTagList = compound.getTagList("particles", Constants.NBT.TAG_COMPOUND);
        Object2DoubleMap<Fluid> newParticles = new Object2DoubleLinkedOpenHashMap<>();
        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            newParticles.put(
                    FluidRegistry.getFluid(nbtTagList.getCompoundTagAt(i).getString("fluid")),
                    nbtTagList.getCompoundTagAt(i).getDouble("amount"));
        }
        this.particles = newParticles;
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buffer) {
        super.writeInitialSyncData(buffer);

        cleanUp();
        NBTTagCompound compound = new NBTTagCompound(); // TODO: move this to a new class
        NBTTagList nbtTagList = new NBTTagList();
        cleanUp(); // TODO: better not use it here
        for (Object2DoubleMap.Entry<Fluid> entry : this.particles.object2DoubleEntrySet()) {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            fluidCompound.setString("fluid", FluidRegistry.getFluidName(entry.getKey()));
            fluidCompound.setDouble("amount", entry.getDoubleValue());
            nbtTagList.appendTag(fluidCompound);
        }
        compound.setTag("particles", nbtTagList);

        buffer.writeCompoundTag(compound);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buffer) {
        super.receiveInitialSyncData(buffer);

        try {
            NBTTagCompound compound = buffer.readCompoundTag();
            NBTTagList nbtTagList = compound.getTagList("particles", Constants.NBT.TAG_COMPOUND);
            Object2DoubleMap<Fluid> newParticles = new Object2DoubleLinkedOpenHashMap<>();
            for (int i = 0; i < nbtTagList.tagCount(); i++) {
                newParticles.put(
                        FluidRegistry.getFluid(nbtTagList.getCompoundTagAt(i).getString("fluid")),
                        nbtTagList.getCompoundTagAt(i).getDouble("amount"));
            }
            this.particles = newParticles;
        } catch (Exception ignored) {}
    }

    @Override
    public String getName() {
        return "PressureContainer";
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability) {
        if (capability == GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER) {
            return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(this);
        }
        return null;
    }
}
