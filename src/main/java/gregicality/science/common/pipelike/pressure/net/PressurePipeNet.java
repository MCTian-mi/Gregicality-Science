package gregicality.science.common.pipelike.pressure.net;

import gregicality.GCYSInternalTags;
import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.impl.GasMap;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.common.pipelike.pressure.PressurePipeType;
import gregtech.api.pipenet.Node;
import gregtech.api.pipenet.PipeNet;
import gregtech.api.pipenet.WorldPipeNet;
import gregtech.api.pipenet.tickable.TickableWorldPipeNet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.Map;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import static gregtech.api.unification.material.Materials.Air;

@Mod.EventBusSubscriber(modid = GCYSInternalTags.MODID, value = Side.SERVER)
public class PressurePipeNet extends PipeNet<PressurePipeProperties> implements IPressureContainer, ITickable {

    private final GasMap gasMap;
    private int volume = 0;
    private double minNetPressure = Double.MAX_VALUE;
    private double maxNetPressure = Double.MIN_VALUE;

    private double leakRate = 0.0;

    public PressurePipeNet(TickableWorldPipeNet<PressurePipeProperties, PressurePipeNet> world) {
        super(world);
        this.gasMap = new GasMap();
//        this.gasMap.pushGas(Air.getFluid(), volume * GCYSValues.EARTH_PRESSURE);
    }

    @Override
    protected void writeNodeData(@Nonnull PressurePipeProperties pressurePipeProperties, @Nonnull NBTTagCompound nbt) {
        nbt.setDouble("MinP", pressurePipeProperties.getMinPressure());
        nbt.setDouble("MaxP", pressurePipeProperties.getMaxPressure());
        nbt.setDouble("Volume", pressurePipeProperties.getVolume());
    }

    @Override
    protected PressurePipeProperties readNodeData(@Nonnull NBTTagCompound nbt) {
        int minP = nbt.getInteger("MinP");
        int maxP = nbt.getInteger("MaxP");
        int volume = nbt.getInteger("Volume");
        return new PressurePipeProperties(minP, maxP, volume);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setDouble("minNetP", minNetPressure);
        compound.setDouble("maxNetP", maxNetPressure);
        compound.setDouble("Volume", volume);
        compound.setTag("GasMap", gasMap.serializeNBT());
        compound.setDouble("LeakRate", leakRate);
        return compound;
    }


    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        this.minNetPressure = nbt.getDouble("minNetP");
        this.maxNetPressure = nbt.getDouble("maxNetP");
        this.volume = nbt.getInteger("Volume");
        this.gasMap.deserializeNBT(nbt.getCompoundTag("GasMap"));
        this.leakRate = nbt.getDouble("LeakRate");
    }

    @Override
    protected void onNodeConnectionsUpdate() {
        super.onNodeConnectionsUpdate();
        this.minNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMinPressure()).max().orElse(Double.MAX_VALUE);
        this.maxNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMaxPressure()).min().orElse(Double.MIN_VALUE);
//        final int oldVolume = getVolume();
        this.volume = Math.max(1, getAllNodes().values().stream().mapToInt(node -> node.data.getVolume()).sum());

//        int deltaVolume = getVolume() - oldVolume;
//        if (deltaVolume > 0) {
//            pushGas(Air.getFluid(), deltaVolume * GCYSValues.EARTH_PRESSURE, false);
//        } else if (deltaVolume < 0) {
//            popGas(-deltaVolume * GCYSValues.EARTH_PRESSURE, false);
//        }
    }

    @Override
    public void onPipeConnectionsUpdate() {
        super.onPipeConnectionsUpdate();
        leakRate = PressureNetWalker.getLeakage(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
    }

    @Override
    protected void transferNodeData(Map<BlockPos, Node<PressurePipeProperties>> transferredNodes,
                                    PipeNet<PressurePipeProperties> parentNet) {
        super.transferNodeData(transferredNodes, parentNet);
        if (parentNet instanceof PressurePipeNet pressurePipeNet) {
            transferredNodes.forEach((pos, node) -> {
                pressurePipeNet.volume -= node.data.getVolume();
            });
            IPressureContainer.mergeContainers(pressurePipeNet, this);
        }
    }

    @Override
    protected void addNode(BlockPos nodePos, Node<PressurePipeProperties> node) {
        super.addNode(nodePos, node);
        int deltaVolume = node.data.getVolume();
        pushGas(Air.getFluid(), deltaVolume * GCYSValues.EARTH_PRESSURE, false);
    }

    @Override
    public void removeNode(BlockPos nodePos) {
        double oldPressure = getPressure();
        int deltaVolume = getNodeAt(nodePos).data.getVolume();
        popGas(deltaVolume * oldPressure, false);
        this.volume -= deltaVolume;
        super.removeNode(nodePos);
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

    // TODO We should do this only once per tick, instead of ticking them in TE
    @Deprecated
    public void onLeak(PressurePipeType pipeType) {
        // Calculate the pressure drop per tick
        var k = (-Math.log(getPressure() / GCYSValues.EARTH_PRESSURE)) * (pipeType.getThickness() * 4.0) * GCYSValues.PRESSURE_LEAK_SCALE;

        // And corresponding gas amount...
        k *= getVolume();

        if (k > 0) {
            this.pushGas(Air.getFluid(), k, false);
        } else if (k < 0) {
            this.popGas(-k, false);
        }
    }

    public void onLeakTick() {
        // Calculate the pressure drop
        var k = (-Math.log(getPressure() / GCYSValues.EARTH_PRESSURE)) * leakRate * GCYSValues.PRESSURE_LEAK_SCALE;

        // And corresponding gas amount...
        k *= getVolume();

        // And update rate...
        k *= WorldPressurePipeNet.UPDATE_RATE;

        if (k > 0) {
            this.pushGas(Air.getFluid(), k, false);
        } else if (k < 0) {
            this.popGas(-k, false);
        }
    }

    public double getLeakRate() {
        return leakRate;
    }

    @Override
    public double getMinPressure() {
        return minNetPressure;
    }

    @Override
    public double getMaxPressure() {
        return maxNetPressure;
    }

    @Override
    public void update() {
        onLeakTick();
    }
}
