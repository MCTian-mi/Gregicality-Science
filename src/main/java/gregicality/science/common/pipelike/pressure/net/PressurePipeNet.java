package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.common.pipelike.pressure.PressurePipeData;
import gregtech.api.pipenet.PipeNet;
import gregtech.api.pipenet.WorldPipeNet;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.Map;

import static gregtech.api.unification.material.Materials.Air;

public class PressurePipeNet extends PipeNet<PressurePipeData> implements IPressureContainer {

    private Object2DoubleMap<Fluid> netParticles;
    private int volume = 1;
    private double minNetPressure = Double.MAX_VALUE;
    private double maxNetPressure = Double.MIN_VALUE;

    public PressurePipeNet(WorldPipeNet<PressurePipeData, ? extends PipeNet> world) {
        super(world);
        this.netParticles = new Object2DoubleLinkedOpenHashMap<>();
        this.netParticles.put(Air.getFluid(), volume * GCYSValues.EARTH_PRESSURE);
    }

    @Override
    protected void writeNodeData(@Nonnull PressurePipeData pressurePipeData, @Nonnull NBTTagCompound nbt) {
        nbt.setDouble("MinP", pressurePipeData.getMinPressure());
        nbt.setDouble("MaxP", pressurePipeData.getMaxPressure());
        nbt.setDouble("Volume", pressurePipeData.getVolume());
    }

    @Override
    protected PressurePipeData readNodeData(@Nonnull NBTTagCompound nbt) {
        return new PressurePipeData(nbt.getDouble("MinP"), nbt.getDouble("MaxP"), nbt.getInteger("Volume"));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setDouble("minNetP", minNetPressure);
        compound.setDouble("maxNetP", maxNetPressure);
        compound.setDouble("Volume", volume);
        NBTTagList nbtTagList = new NBTTagList();
        cleanUp(); // TODO: better not use it here
        for (Object2DoubleMap.Entry<Fluid> entry : this.netParticles.object2DoubleEntrySet()) {
            NBTTagCompound fluidCompound = new NBTTagCompound();
            fluidCompound.setString("fluid", FluidRegistry.getFluidName(entry.getKey()));
            fluidCompound.setDouble("amount", entry.getDoubleValue());
            nbtTagList.appendTag(fluidCompound);
        }
        compound.setTag("particles", nbtTagList);
        return compound;
    }


    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        this.minNetPressure = nbt.getDouble("minNetP");
        this.maxNetPressure = nbt.getDouble("maxNetP");
        this.volume = nbt.getInteger("Volume");
        NBTTagList nbtTagList = nbt.getTagList("particles", Constants.NBT.TAG_COMPOUND);
        Object2DoubleMap<Fluid> newParticles = new Object2DoubleLinkedOpenHashMap<>();
        for (int i = 0; i < nbtTagList.tagCount(); i++) {
            newParticles.put(
                    FluidRegistry.getFluid(nbtTagList.getCompoundTagAt(i).getString("fluid")),
                    nbtTagList.getCompoundTagAt(i).getDouble("amount"));
        }
        this.netParticles = newParticles;
    }

    @Override
    protected void onNodeConnectionsUpdate() {
        super.onNodeConnectionsUpdate();
        this.minNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMinPressure()).max().orElse(Double.MAX_VALUE);
        this.maxNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMaxPressure()).min().orElse(Double.MIN_VALUE);
        final double oldVolume = getVolume();
        this.volume = Math.max(1, getAllNodes().values().stream().mapToInt(node -> node.data.getVolume()).sum());
//        this.netParticles *= getVolume() / oldVolume;
    }

    @Override
    public void onPipeConnectionsUpdate() {
        super.onPipeConnectionsUpdate();
    }

    @Override
    public Map<Fluid, Double> getParticleMap() {
        return new Object2DoubleLinkedOpenHashMap<>();
    }

    @Override
    public int getVolume() {
        return volume;
    }

    @Override
    public boolean changeTotalParticles(double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForParticles(getTotalParticles() + amount));
        for (Fluid fluid : getParticleMap().keySet()) {
            setParticles(fluid, getParticles(fluid) + amount * getRatio(fluid));
        }
        PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        return isPressureSafe();
    }

    public void onLeak() {
        if (getPressure() < GCYSValues.EARTH_PRESSURE) this.changeTotalParticles(getLeakRate(), false);
        else if (getPressure() > GCYSValues.EARTH_PRESSURE) this.changeTotalParticles(-getLeakRate(), false);
    }

    public double getLeakRate() {
        return 1000.0D;
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
