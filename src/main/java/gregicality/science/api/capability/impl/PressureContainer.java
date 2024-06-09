package gregicality.science.api.capability.impl;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregtech.api.metatileentity.MTETrait;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static gregtech.api.unification.material.Materials.Air;

public class PressureContainer extends MTETrait implements IPressureContainer {

    private final double minPressure;
    private final double maxPressure;
    private final int volume;

    private final GasMap gasMap;

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
        this.gasMap = new GasMap();
        this.gasMap.pushGas(Air.getFluid(), volume * GCYSValues.EARTH_PRESSURE);
    }

    @Override
    public double getMaxPressure() {
        return this.maxPressure;
    }

    @Override
    public GasMap getGasMap() {
        return gasMap;
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
    public @NotNull NBTTagCompound serializeNBT() {
        return this.gasMap.serializeNBT();
    }

    @Override
    public void deserializeNBT(@Nonnull NBTTagCompound compound) {
        this.gasMap.deserializeNBT(compound);
    }

    @Override
    public void writeInitialSyncData(@NotNull PacketBuffer buffer) {
        super.writeInitialSyncData(buffer);
        buffer.writeCompoundTag(gasMap.serializeNBT());
    }

    @Override
    public void receiveInitialSyncData(@NotNull PacketBuffer buffer) {
        super.receiveInitialSyncData(buffer);
        try {
            NBTTagCompound compound = buffer.readCompoundTag();
            this.gasMap.deserializeNBT(compound);
        } catch (Exception ignored) {
        }
    }

    @Override
    public @NotNull String getName() {
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
