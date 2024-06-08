package gregicality.science.api.capability;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.impl.GasMap;
import gregtech.common.ConfigHolder;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

public interface IPressureContainer extends INBTSerializable<NBTTagCompound> {

    int PRESSURE_TOLERANCE = 5000;
    IPressureContainer EMPTY = new IPressureContainer() {
        @Override
        public NBTTagCompound serializeNBT() {
            return null;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
        }

        @Override
        public GasMap getGasMap() {
            return GasMap.EMPTY;
        }

        @Override
        public double getGasAmount() {
            return 0;
        }

        @Override
        public void setGasAmount(Fluid fluidkey, double amount) {/**/}

        @Override
        public int getVolume() {
            return 1;
        }

        @Override
        public double getMinPressure() {
            return GCYSValues.EARTH_PRESSURE / 2;
        }

        @Override
        public double getMaxPressure() {
            return GCYSValues.EARTH_PRESSURE * 2;
        }
    };

    static void mergeContainers(@Nonnull IPressureContainer... containers) {
        mergeContainers(true, containers);
    }

    static void mergeContainers(boolean checkSafety, @Nonnull IPressureContainer... containers) {
        // P = (n1 + n2) / (v1 + v2)
        double totalParticles = 0;
        int totalVolume = 0;
        GasMap totalMap = new GasMap();

        for (IPressureContainer container : containers) {
            totalParticles += container.getGasAmount();
            totalVolume += container.getVolume();
            container.getGasMap().forEach(totalMap::pushGas);
        }
        if (totalVolume == 0 || totalParticles == 0) return;

        final double newPressure = totalParticles / totalVolume;

        if (checkSafety) {
            for (IPressureContainer container : containers) {
                if (!container.isPressureSafe(newPressure)) {
                    totalParticles -= container.getGasAmount();
                    totalVolume -= container.getVolume();
                    container.getGasMap().forEach(totalMap::popGas);
                }
            }
        }

        totalMap.cleanUp(); // TODO: move this elsewhere...?
        // P = vN * [(n1 + n2 + ...) / (v1 + v2 + ...)] / vN

        for (IPressureContainer container : containers) {
            container.cleanUp();
            if (!checkSafety || container.isPressureSafe(newPressure)) {
                for (Object2DoubleMap.Entry<Fluid> entry : totalMap.object2DoubleEntrySet()) {
                    container.setGasAmount(entry.getKey(), entry.getDoubleValue() * container.getVolume() / totalVolume);
                }
            }
        }
    }

    GasMap getGasMap();

    default void clear() {
        getGasMap().clear();
    }

    default double getGasAmount() {
        return getGasMap().getTotalGasAmount();
    }

    ;

    default double getGasAmount(Fluid fluid) {
        return getGasMap().getGasAmount(fluid);
    }

    ;

    default void setGasAmount(Fluid fluid, double amount) {
        getGasMap().setGasAmount(fluid, amount);
    }

    ;

    int getVolume();

    default double getPressure() {
        return getGasAmount() / getVolume();
    }

    default double getPressure(Fluid fluid) {
        return getGasAmount(fluid) / getVolume();
    }

    default double getRatio(Fluid fluid) {
        return getGasMap().getRatio(fluid);
    }

    default void cleanUp() {
        getGasMap().cleanUp();
    }

    default double getPressureForGasAmount(double amount) {
        return amount / getVolume();
    }

    default double getPressureForVolume(double volume) {
        return getGasAmount() / volume;
    }

    default boolean pushGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() + amount));
        getGasMap().pushGas(fluid, amount);
        return isPressureSafe();
    }

    default boolean popGas(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() - amount));
        if (getGasAmount(fluid) < amount) return false;
        getGasMap().popGas(fluid, amount);
        return isPressureSafe();
    }

    default boolean popGas(double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForGasAmount(getGasAmount() - amount));
        if (getGasAmount() < amount) return false;
        getGasMap().popGas(amount);
        return isPressureSafe();
    }

    /**
     * @return the minimum pressure this container can handle
     */
    double getMinPressure();

    /**
     * @return the maximum pressure this container can handle
     */
    double getMaxPressure();

    /**
     * @return true if the pressure is safe for the container, else false
     */
    default boolean isPressureSafe() {
//        cleanUp(); // TODO: move this elsewhere
        return isPressureSafe(getPressure());
    }

    /**
     * @param pressure the pressure to check
     * @return true if the pressure is safe for the container, else false
     */
    default boolean isPressureSafe(double pressure) {
        return pressure <= getMaxPressure() && pressure >= getMinPressure();
    }

    /**
     * @return if the pressure is around atmospheric levels
     */
    default boolean isNormalPressure() {
        return Math.abs(getPressure() - GCYSValues.EARTH_PRESSURE) < PRESSURE_TOLERANCE;
    }

    /**
     * @return if the pressure is a vacuum
     */
    default boolean isVacuum() {
        return getPressure() < GCYSValues.EARTH_PRESSURE - PRESSURE_TOLERANCE;
    }

    /**
     * Causes an explosion due to pressure
     *
     * @param world the world to create the explosion in
     * @param pos   the position of the explosion
     */
    default void causePressureExplosion(World world, BlockPos pos) {
        if (world != null && !world.isRemote) {
            final float explosionPower = (float) Math.abs(Math.abs(Math.log10(getPressure())) - 4);
            world.setBlockToAir(pos);

            if (isVacuum()) {
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        explosionPower, false);
            } else {
                world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        explosionPower, ConfigHolder.machines.doesExplosionDamagesTerrain);
            }
        }
    }

    /**
     * @param trackVacuum true if percentage should be tracked in relation to the min pressure, else the max
     * @return a double from 0.0 to 1.0 representing how close the current pressure is to the max or min
     */
    default double getPressurePercent(boolean trackVacuum) {
        if (getPressure() == 0) return 0;
        if (getMaxPressure() - getMinPressure() == 0) return 1.0D;
        double min = Math.log10(getMinPressure());
        double percent = (Math.log10(getPressure()) - min) / (Math.log10(getMaxPressure()) - min);
        return trackVacuum ? 1.0D - percent : percent;
    }
}
