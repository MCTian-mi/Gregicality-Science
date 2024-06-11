package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.impl.GasMap;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregtech.api.pipenet.PipeNet;
import gregtech.api.pipenet.WorldPipeNet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

import static gregtech.api.unification.material.Materials.Air;

public class PressurePipeNet extends PipeNet<PressurePipeProperties> implements IPressureContainer {

    private final GasMap gasMap;
    private int volume = 1;
    private double minNetPressure = Double.MAX_VALUE;
    private double maxNetPressure = Double.MIN_VALUE;

    public PressurePipeNet(WorldPipeNet<PressurePipeProperties, PressurePipeNet> world) {
        super(world);
        this.gasMap = new GasMap();
        this.gasMap.pushGas(Air.getFluid(), volume * GCYSValues.EARTH_PRESSURE);
    }

    @Override
    protected void writeNodeData(@Nonnull PressurePipeProperties pressurePipeProperties, @Nonnull NBTTagCompound nbt) {
        nbt.setDouble("MinP", pressurePipeProperties.getMinPressure());
        nbt.setDouble("MaxP", pressurePipeProperties.getMaxPressure());
        nbt.setDouble("Volume", pressurePipeProperties.getVolume());
        nbt.setFloat("PressureTightness", pressurePipeProperties.getPressureTightness());
    }

    @Override
    protected PressurePipeProperties readNodeData(@Nonnull NBTTagCompound nbt) {
        int minP = nbt.getInteger("MinP");
        int maxP = nbt.getInteger("MaxP");
        int volume = nbt.getInteger("Volume");
        float pressureTightness = nbt.getFloat("PressureTightness");
        return new PressurePipeProperties(minP, maxP, volume, pressureTightness);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setDouble("minNetP", minNetPressure);
        compound.setDouble("maxNetP", maxNetPressure);
        compound.setDouble("Volume", volume);
        compound.setTag("GasMap", gasMap.serializeNBT());
        return compound;
    }


    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        this.minNetPressure = nbt.getDouble("minNetP");
        this.maxNetPressure = nbt.getDouble("maxNetP");
        this.volume = nbt.getInteger("Volume");
        this.gasMap.deserializeNBT(nbt.getCompoundTag("GasMap"));
    }

    @Override
    protected void onNodeConnectionsUpdate() {
        super.onNodeConnectionsUpdate();
        this.minNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMinPressure()).max().orElse(Double.MAX_VALUE);
        this.maxNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMaxPressure()).min().orElse(Double.MIN_VALUE);
        final int oldVolume = getVolume();
        this.volume = Math.max(1, getAllNodes().values().stream().mapToInt(node -> node.data.getVolume()).sum());
        int deltaVolume = getVolume() - oldVolume;
        if (deltaVolume > 0) {
            pushGas(Air.getFluid(), deltaVolume * GCYSValues.EARTH_PRESSURE, false);
        } else if (deltaVolume < 0) {
            popGas(-deltaVolume * GCYSValues.EARTH_PRESSURE, false);
        }
    }

    @Override
    public void onPipeConnectionsUpdate() {
        super.onPipeConnectionsUpdate();
    }

    @Override
    public GasMap getGasMap() {
        return this.gasMap;
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public boolean popGas(double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() - amount));
        if (getGasAmount() < amount) return false;
        getGasMap().popGas(amount);
        if (!getAllNodes().isEmpty()) {
            PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        }
        return isPressureSafe();
    }

    @Override
    public boolean popGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() - amount));
        if (getGasAmount(fluid) < amount) return false;
        getGasMap().popGas(fluid, amount);
        if (!getAllNodes().isEmpty()) {
            PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        }
        return isPressureSafe();
    }

    @Override
    public boolean pushGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() + amount));
        getGasMap().pushGas(fluid, amount);
        if (!getAllNodes().isEmpty()) {
            PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        }
        return isPressureSafe();
    }

    public void onLeak() {
        if (getPressure() < GCYSValues.EARTH_PRESSURE) this.popGas(getLeakRate(), false);
        else if (getPressure() > GCYSValues.EARTH_PRESSURE) this.pushGas(Air.getFluid(), getLeakRate(), false);
    }

    public double getLeakRate() {
        return 0D; // TODO make this depend on exposed faces
    }

    @Override
    public double getMinPressure() {
        return minNetPressure;
    }

    @Override
    public double getMaxPressure() {
        return maxNetPressure;
    }
}
