package gregicality.science.common.pipelike.pressure.tile;

import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.common.pipelike.pressure.PressurePipeType;
import gregicality.science.common.pipelike.pressure.net.PressurePipeNet;
import gregicality.science.common.pipelike.pressure.net.WorldPressurePipeNet;
import gregtech.api.GTValues;
import gregtech.api.pipenet.block.material.TileEntityMaterialPipeBase;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.util.TaskScheduler;

import java.util.EnumSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

import org.jetbrains.annotations.NotNull;

public class TileEntityPressurePipe extends TileEntityMaterialPipeBase<PressurePipeType, PressurePipeProperties> {

    public static final int FREQUENCY = 5;
    private WeakReference<PressurePipeNet> currentPipeNet = new WeakReference<>(null);

    public static void spawnParticles(World worldIn, BlockPos pos, EnumFacing direction, EnumParticleTypes particleType, // TODO
                                      int particleCount) {
        if (worldIn instanceof WorldServer) {
            ((WorldServer) worldIn).spawnParticle(particleType,
                   pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    particleCount,
                    direction.getXOffset() + GTValues.RNG.nextDouble() * 0.1,
                    direction.getYOffset() + GTValues.RNG.nextDouble() * 0.1,
                    direction.getZOffset() + GTValues.RNG.nextDouble() * 0.1,
                    0.1);
        }
    }

    private final EnumSet<EnumFacing> leakingFaces = EnumSet.noneOf(EnumFacing.class);

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
            return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(getPressurePipeNet());
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
        PressurePipeNet net = getPressurePipeNet();
        if (net != null) net.causePressureExplosion(getWorld(), getPos());
    }

    public void leakDetect(EnumFacing side, boolean connected) {
        BlockPos blockPos = pos.offset(side);
        IBlockState neighbour = world.getBlockState(blockPos);
        leakingFaces.remove(side);
        if (!connected) return;

        if (world.getTileEntity(blockPos) instanceof IPipeTile<?,?> te)
            if (te.getPipeBlock().getPipeTypeClass() == this.getPipeTypeClass() && te.isConnected(side.getOpposite())) {
                if (te.getPipeType().getThickness() != getPipeType().getThickness()) {
                    // mismatched connected pipe sizes leak
                    leakingFaces.add(side);
                    if (!isParticlesRendering)
                        TaskScheduler.scheduleTask(getWorld(), this::updateLeakage);
                }
                return;
            }

        // check the pipes for unconnected things
        if (!neighbour.isFullBlock() || !neighbour.isOpaqueCube()) {
            leakingFaces.add(side);
            if (!isParticlesRendering)
                TaskScheduler.scheduleTask(getWorld(), this::updateLeakage);
        }
    }

    @Override
    public void setConnection(EnumFacing side, boolean connected, boolean fromNeighbor) {
        super.setConnection(side, connected, fromNeighbor);
        if (!world.isRemote) {
            leakDetect(side, connected);
        }
    }

    @Override
    public boolean supportsTicking() {
        return false;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        var connections = compound.getInteger("LeakingFaces");
        for (EnumFacing facing : EnumFacing.values()) {
            if (isConnected(connections, facing)) {
                leakingFaces.add(facing);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!getWorld().isRemote)
            TaskScheduler.scheduleTask(getWorld(), this::updateLeakage);
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        var connections = 0;
        for (EnumFacing facing : leakingFaces) {
            connections = withSideConnection(connections, facing, true);
        }
        compound.setInteger("LeakingFaces", connections);
        return super.writeToNBT(compound);
    }

    private boolean isParticlesRendering = false;

    public boolean updateLeakage() {
        isParticlesRendering = true;
        PressurePipeNet net = getPressurePipeNet();
        if (net != null) {
            if (!net.isPressureSafe()) causePressureExplosion();
            else if (!net.isNormalPressure()) {
                for (EnumFacing face : leakingFaces) {
                    spawnParticles(getWorld(), getPos(), face, EnumParticleTypes.CLOUD, (int)(getPipeType().getThickness() * 2));
                }
            }
            if (net.isNormalPressure() || leakingFaces.isEmpty()) {
                isParticlesRendering = false;
                return false;
            } else {
                return isParticlesRendering;
            }
        }
        return isParticlesRendering;
    }

    public PressurePipeNet getPressurePipeNet() {
        if (world == null || world.isRemote) return null;
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
