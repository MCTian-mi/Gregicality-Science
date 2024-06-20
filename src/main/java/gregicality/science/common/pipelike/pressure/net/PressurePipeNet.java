package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.impl.GasMap;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregtech.api.pipenet.Node;
import gregtech.api.pipenet.PipeNet;
import gregtech.api.pipenet.WorldPipeNet;
import gregtech.api.util.FacingPos;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.Map;

import static gregtech.api.unification.material.Materials.Air;

public class PressurePipeNet extends PipeNet<PressurePipeProperties> implements IPressureContainer {

    private final GasMap gasMap;
    private int volume = 0;
    //    private final Map<BlockPos, ArrayList<EnumFacing>> leakingFaces;
    private final Map<FacingPos, Double> leakingRates;
    private double totalLeakingRate = 0;
    private double minNetPressure = Double.MAX_VALUE;
    private double maxNetPressure = Double.MIN_VALUE;

    public PressurePipeNet(WorldPipeNet<PressurePipeProperties, PressurePipeNet> world) {
        super(world);
        this.gasMap = new GasMap();
//        this.leakingFaces = new Object2ObjectArrayMap<>();
        this.leakingRates = new Object2DoubleArrayMap<>();
    }

    @Override
    protected void writeNodeData(@Nonnull PressurePipeProperties pressurePipeProperties, @Nonnull NBTTagCompound nbt) {
        nbt.setDouble("MinP", pressurePipeProperties.getMinPressure());
        nbt.setDouble("MaxP", pressurePipeProperties.getMaxPressure());
        nbt.setDouble("Volume", pressurePipeProperties.getVolume());
    }

    @Override
    protected PressurePipeProperties readNodeData(@Nonnull NBTTagCompound nbt) {
        return new PressurePipeProperties(nbt.getInteger("MinP"),
                nbt.getInteger("MaxP"),
                nbt.getInteger("Volume"));
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
        // TODO ig we don't need to do this in such a brute force way
        this.minNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMinPressure()).max().orElse(Double.MAX_VALUE);
        this.maxNetPressure = getAllNodes().values().stream().mapToDouble(node -> node.data.getMaxPressure()).min().orElse(Double.MIN_VALUE);
        this.volume = Math.max(1, getAllNodes().values().stream().mapToInt(node -> node.data.getVolume()).sum());
    }

    @Override
    public void onPipeConnectionsUpdate() {
        super.onPipeConnectionsUpdate();
    }

    @Override
    protected void transferNodeData(Map<BlockPos, Node<PressurePipeProperties>> transferredNodes,
                                    PipeNet<PressurePipeProperties> parentNet) {
        super.transferNodeData(transferredNodes, parentNet);
        if (parentNet instanceof PressurePipeNet pressurePipeNet) {
            var parentLeakingRates = pressurePipeNet.leakingRates;
            transferredNodes.forEach((pos, node) -> {
                pressurePipeNet.volume -= node.data.getVolume();

                // TODO there must be better way to do this
                for (EnumFacing face : EnumFacing.VALUES) {
                    var facingPos = new FacingPos(pos, face);
                    if (parentLeakingRates.containsKey(facingPos)) {
                        var rate = parentLeakingRates.remove(facingPos);
                        leakingRates.put(facingPos, rate);
                        this.totalLeakingRate += rate;
                        pressurePipeNet.totalLeakingRate -= rate;
                    }
                }
            });
            IPressureContainer.mergeContainers(pressurePipeNet, this);
        }
    }

    @Override
    protected void addNode(BlockPos nodePos, Node<PressurePipeProperties> node) {
        super.addNode(nodePos, node);
        int deltaVolume = node.data.getVolume();
        pushGas(Air.getFluid(), deltaVolume * GCYSValues.EARTH_PRESSURE, false);
        var selfNode = getNodeAt(nodePos);

        // TODO this is a very inefficient way to do this
        for (EnumFacing face : EnumFacing.VALUES) {

            // adds the pipe's own leaking faces
//            var facingPos = new FacingPos(nodePos, face);
//            if (leakingRates.containsKey(facingPos)) {
//                totalLeakingRate -= leakingRates.remove(facingPos);
//            }

            // removes the leaking faces of the neighboring pipes
            var offsetPos = nodePos.offset(face);
            var offsetFacingPos = new FacingPos(offsetPos, face.getOpposite());
            if (containsNode(offsetPos)) {
                Node<PressurePipeProperties> secondNode = getNodeAt(offsetPos);
                if (canNodesConnect(selfNode, face, secondNode, this)
                        && leakingRates.containsKey(offsetFacingPos)) {
                    totalLeakingRate -= leakingRates.remove(offsetFacingPos);
                }
            }
        }
    }

    @Override
    public void removeNode(BlockPos nodePos) {

        // TODO this is a very inefficient way to do this
        var selfNode = getNodeAt(nodePos);
        for (EnumFacing face : EnumFacing.VALUES) {

            // removes the pipe's own leaking faces
            var facingPos = new FacingPos(nodePos, face);
            if (leakingRates.containsKey(facingPos)) {
                totalLeakingRate -= leakingRates.remove(facingPos);
            }

            // adds the leaking faces of the neighboring pipes
            var offsetPos = nodePos.offset(face);
            if (containsNode(offsetPos)) {
                Node<PressurePipeProperties> secondNode = getNodeAt(offsetPos);
                if (canNodesConnect(selfNode, face, secondNode, this)) {
                    leakingRates.put(new FacingPos(offsetPos, face.getOpposite()), 10d); // TODO calculate the leaking rate
                    totalLeakingRate += 10d;
                }
            }
        }
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
        if (!getAllNodes().isEmpty() && !isPressureSafe()) {
            PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        }
        return isPressureSafe();
    }

    @Override
    public boolean popGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() - amount));
        if (getGasAmount(fluid) < amount) return false;
        getGasMap().popGas(fluid, amount);
        if (!getAllNodes().isEmpty() && !isPressureSafe()) {
            PressureNetWalker.checkPressure(getWorldData(), getAllNodes().keySet().iterator().next(), getPressure());
        }
        return isPressureSafe();
    }

    @Override
    public boolean pushGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() + amount));
        getGasMap().pushGas(fluid, amount);
        if (!getAllNodes().isEmpty() && !isPressureSafe()) {
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
