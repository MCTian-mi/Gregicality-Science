package gregicality.science.common.pipelike.pressure.tile;

import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.common.pipelike.pressure.PressurePipeType;
import gregicality.science.common.pipelike.pressure.net.PressurePipeNet;
import gregicality.science.common.pipelike.pressure.net.WorldPressurePipeNet;
import gregtech.api.pipenet.block.material.TileEntityMaterialPipeBase;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.util.TaskScheduler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class TileEntityPressurePipe extends TileEntityMaterialPipeBase<PressurePipeType, PressurePipeProperties> {

    public static final int FREQUENCY = 5;
    private WeakReference<PressurePipeNet> currentPipeNet = new WeakReference<>(null);

    public static void spawnParticles(World worldIn, BlockPos pos, EnumFacing direction, EnumParticleTypes particleType, // TODO
                                      int particleCount) {
//        if (worldIn instanceof WorldServer) {
//            ((WorldServer) worldIn).spawnParticle(particleType,
//                    pos.getX() + 0.5,
//                    pos.getY() + 0.5,
//                    pos.getZ() + 0.5,
//                    particleCount,
//                    direction.getXOffset() * 0.2 + GTValues.RNG.nextDouble() * 0.1,
//                    direction.getYOffset() * 0.2 + GTValues.RNG.nextDouble() * 0.1,
//                    direction.getZOffset() * 0.2 + GTValues.RNG.nextDouble() * 0.1,
//                    0.1);
//        }
    }

    @Override
    public Class<PressurePipeType> getPipeTypeClass() {
        return PressurePipeType.class;
    }

    @Override
    public <T> T getCapabilityInternal(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER) {
            if (world == null || world.isRemote) {
                return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(IPressureContainer.EMPTY);
            }
            return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(getPipeNet());
        }
        return super.getCapabilityInternal(capability, facing);
    }

    public void checkPressure(double pressure) {
        if (pressure > getNodeData().getMaxPressure()) {
            causePressureExplosion();
        } else if (pressure < getNodeData().getMinPressure()) {
            causePressureExplosion();
        }
    }

    public void causePressureExplosion() {
        PressurePipeNet net = getPipeNet();
        if (net != null) net.causePressureExplosion(getWorld(), getPos());
    }

    @Override
    public void setConnection(EnumFacing side, boolean connected, boolean fromNeighbor) {
        super.setConnection(side, connected, fromNeighbor);
        if (!world.isRemote) {
            BlockPos blockPos = pos.offset(side);
            IBlockState neighbour = world.getBlockState(blockPos);
            if (!neighbour.isFullBlock() || !neighbour.isOpaqueCube()) {
                // check the pipes for unconnected things
                TaskScheduler.scheduleTask(getWorld(), this::updateLeakage);
                return;
            }
            TileEntity te = world.getTileEntity(blockPos);
            if (te instanceof IPipeTile && ((IPipeTile<?, ?>) te).getPipeBlock().getPipeTypeClass() == this.getPipeTypeClass() &&
                    ((IPipeTile<?, ?>) te).getPipeType().getThickness() != getPipeType().getThickness() &&
                    ((IPipeTile<?, ?>) te).isConnected(side.getOpposite())) {
                // mismatched connected pipe sizes leak
                TaskScheduler.scheduleTask(getWorld(), this::updateLeakage);
            }
        }
    }

    @Override
    public boolean supportsTicking() {
        return false;
    }

    public boolean updateLeakage() {
        PressurePipeNet net = getPipeNet();
        if (net != null) {
            net.onLeak();
            if (!net.isPressureSafe()) causePressureExplosion();
            return !net.isNormalPressure();
        }
        return true;
    }

    public PressurePipeNet getPipeNet() {
        if (world == null || world.isRemote)
            return null;
        PressurePipeNet currentPipeNet = this.currentPipeNet.get();
        if (currentPipeNet != null && currentPipeNet.isValid() &&
                currentPipeNet.containsNode(getPipePos()))
            return currentPipeNet; // if current net is valid and does contain position, return it
        WorldPressurePipeNet worldPressurePipeNet = (WorldPressurePipeNet) getPipeBlock().getWorldPipeNet(getPipeWorld());
        currentPipeNet = worldPressurePipeNet.getNetFromPos(getPipePos());
        if (currentPipeNet != null) {
            this.currentPipeNet = new WeakReference<>(currentPipeNet);
        }
        return currentPipeNet;
    }
}
