package gregicality.science.api.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;

public interface IPressureMachine {

    /**
     * @return the pressure container of this machine
     */
    IPressureContainer getPressureContainer();

    World getWorld();

    BlockPos getPos();

    default void balancePressure() {
        double oldPressure = getPressureContainer().getPressure();
        ArrayList<IPressureContainer> containers = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
            if (tile != null) {
                IPressureContainer container = tile.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, facing.getOpposite());
                if (container != null) {
                    containers.add(container);
                }
            }
        }
        if (containers.isEmpty()) return;
        containers.add(this.getPressureContainer());
        IPressureContainer.mergeContainers(false, containers.toArray(new IPressureContainer[0]));

        double newPressure = getPressureContainer().getPressure();
        if (!getPressureContainer().isPressureSafe(newPressure)) {
            getPressureContainer().causePressureExplosion(getWorld(), getPos()); // TODO
        }
        adjustTickRate(oldPressure, newPressure);
    }

    void setTickRate(int rate);

    int getTickRate();

    default int getDefaultTickRate() {
        return 20;
    }

    default int getMaxTickRate() {
        return 2;
    }

    default int getMinTickRate() {
        return 100;
    }

    default void adjustTickRate(double oldPressure, double newPressure) {
        if (newPressure > 2 * oldPressure || newPressure < 0.5 * oldPressure) {
            setTickRate(MathHelper.clamp(getTickRate() * 2, getMinTickRate(), getMaxTickRate()));
        } else {
            setTickRate(MathHelper.clamp(getTickRate() / 2, getMinTickRate(), getMaxTickRate()));
        }
    }
}
