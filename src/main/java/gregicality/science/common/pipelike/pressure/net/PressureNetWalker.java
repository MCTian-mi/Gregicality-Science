package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.common.pipelike.pressure.tile.TileEntityPressurePipe;
import gregtech.api.pipenet.PipeNetWalker;
import gregtech.api.pipenet.tile.IPipeTile;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PressureNetWalker extends PipeNetWalker<TileEntityPressurePipe> {
    private double pressure = -1;
    private double leakage = 0.0;

    protected PressureNetWalker(World world, BlockPos sourcePipe, int walkedBlocks) {
        super(world, sourcePipe, walkedBlocks);
    }

    public static void checkPressure(World world, BlockPos start, double pressure) {
        PressureNetWalker walker = new PressureNetWalker(world, start, 0);
        walker.pressure = pressure;
        walker.traversePipeNet();
    }

    public static double getLeakage(World world, BlockPos start, double pressure) {
        PressureNetWalker walker = new PressureNetWalker(world, start, 0);
        walker.pressure = pressure;
        walker.traversePipeNet();
        return walker.leakage;
    }

    @Override
    protected PipeNetWalker<TileEntityPressurePipe> createSubWalker(World world, EnumFacing enumFacing, BlockPos blockPos, int i) {
        PressureNetWalker walker = new PressureNetWalker(world, blockPos, i);
        walker.pressure = pressure;
        return walker;
    }

    @Override
    protected void checkPipe(TileEntityPressurePipe pipeTile, BlockPos blockPos) {
        pipeTile.checkPressure(pressure);
    }

    @Override
    protected void checkNeighbour(TileEntityPressurePipe pipeTile, BlockPos pipePos, EnumFacing enumFacing, @Nullable TileEntity tileEntity) {
        var world = pipeTile.getWorld();
        var neighbour = world.getBlockState(pipePos.offset(enumFacing));
        var pipeType = pipeTile.getPipeType();

        if (tileEntity instanceof IPipeTile<?,?> te) {
            if (te.getPipeBlock().getPipeTypeClass() == pipeTile.getPipeTypeClass() &&
                    te.getPipeType().getThickness() != pipeTile.getPipeType().getThickness() &&
                    te.isConnected(enumFacing.getOpposite())) {
                leakage += pipeType.thickness * 4;
            }
        }
        if (!neighbour.isFullBlock() || !neighbour.isOpaqueCube()) {
            if (!pipeType.isSealed()) {
                leakage += pipeType.thickness * 4;
            }
        }
    }

    @Override
    protected Class<TileEntityPressurePipe> getBasePipeClass() {
        return TileEntityPressurePipe.class;
    }
}
