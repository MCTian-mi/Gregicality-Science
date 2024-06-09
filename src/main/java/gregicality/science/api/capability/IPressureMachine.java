package gregicality.science.api.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPressureMachine {

    /**
     * @return the pressure container of this machine
     */
    IPressureContainer getPressureContainer();

    World getWorld();

    BlockPos getPos();

    default void balancePressure() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
            if (tile != null) {
                IPressureContainer container = tile.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, facing.getOpposite());
                if (container != null) {
                    IPressureContainer.mergeContainers(false, this.getPressureContainer(), container);
                }
            }
        }
        if (!getPressureContainer().isPressureSafe())
            getPressureContainer().causePressureExplosion(getWorld(), getPos()); // TODO
    }
}
