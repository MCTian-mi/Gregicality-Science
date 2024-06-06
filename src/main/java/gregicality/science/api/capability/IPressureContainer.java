package gregicality.science.api.capability;

import gregicality.science.api.GCYSValues;
import gregtech.common.ConfigHolder;
import it.unimi.dsi.fastutil.objects.Object2DoubleLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public interface IPressureContainer {

    int PRESSURE_TOLERANCE = 5000;
    double MIN_RATIO = 0.001; // Gases with a ratio less than this will get removed
    IPressureContainer EMPTY = new IPressureContainer() {
        @Override
        public Map<Fluid, Double> getParticleMap() {
            return new Object2DoubleLinkedOpenHashMap<>();
        }

        @Override
        public double getTotalParticles() {
            return 0;
        }

        @Override
        public void setParticles(Fluid fluidkey, double amount) {/**/}

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


    Map<Fluid, Double> getParticleMap();

    /**
     * Equalizes the pressure between containers. This does not modify volume.
     *
     * @param containers the containers to merge
     */
    static void mergeContainers(@Nonnull IPressureContainer... containers) {
        mergeContainers(true, containers);
    }

    /**
     * Equalizes the pressure between containers. This does not modify volume.
     *
     * @param checkSafety whether to check if changing pressure is safe before modifying container values
     * @param containers  the containers to merge
     */
    static void mergeContainers(boolean checkSafety, @Nonnull IPressureContainer... containers) {
        // P = (n1 + n2) / (v1 + v2)
        double totalParticles = 0;
        int totalVolume = 0;
        Object2DoubleMap<Fluid> totalMap = new Object2DoubleLinkedOpenHashMap<>();

        for (IPressureContainer container : containers) {
            totalParticles += container.getTotalParticles();
            totalVolume += container.getVolume();
            container.getParticleMap().forEach(
                    (fluid, amount) -> totalMap.put(fluid, container.getParticles(fluid) + amount));
        }
        if (totalVolume == 0 || totalParticles == 0) return;

        final double newPressure = totalParticles / totalVolume;

        if (checkSafety) {
            for (IPressureContainer container : containers) {
                if (!container.isPressureSafe(newPressure)) {
                    totalParticles -= container.getTotalParticles();
                    totalVolume -= container.getVolume();
                }
            }
        }

        // P = vN * [(n1 + n2 + ...) / (v1 + v2 + ...)] / vN

        for (IPressureContainer container : containers) {
            if (!checkSafety || container.isPressureSafe(newPressure)) {
                for (Object2DoubleMap.Entry<Fluid> entry : totalMap.object2DoubleEntrySet()) {
                    container.setParticles(entry.getKey(), entry.getDoubleValue() * container.getVolume() / totalVolume);
                }
            }
        }
    }

    /**
     * @return the amount of particles in the container
     */
    default double getTotalParticles() {
        return getParticleMap().values().stream().reduce(Double::sum).orElse(0d);
    };

    /**
     * @return the amount of particles in the container
     */
    default double getParticles(Fluid fluid) {
        return getParticleMap().getOrDefault(fluid, 0d);
    };

    /**
     * Set the amount of particles in the container
     *
     * @param amount the amount to set
     */
    default void setParticles(Fluid fluid, double amount) {
        getParticleMap().put(fluid, getParticles(fluid) + amount);
    };

    /**
     * This method should <b>never</b> return 0.
     *
     * @return the volume of the container in B
     */
    int getVolume();

    /**
     * <p>
     * Pressure = number of particles / volume
     * </p>
     * <p>
     * While not scientifically accurate, it provides enough detail for proper equalization
     *
     * @return the amount of pressure in the container
     */
    default double getPressure() {
        return getTotalParticles() / getVolume();
    }


    /**
     * <p>
     * Pressure = number of particles / volume
     * </p>
     * <p>
     * While not scientifically accurate, it provides enough detail for proper equalization
     *
     * @return the amount of pressure in the container
     */
    default double getPartialPressure(Fluid fluid) {
        return getParticles(fluid) / getVolume();
    }

    default double getRatio(Fluid fluid) {
        return getParticles(fluid) / getTotalParticles();
    }

    default void cleanUp() {
        for (Fluid fluid : getParticleMap().keySet()) {
            if (getRatio(fluid) < MIN_RATIO) {
                getParticleMap().remove(fluid);
            };
        }
    }

    /**
     * @param amount the amount of particles
     * @return the pressure if the container had a certain number of particles
     */
    default double getPressureForParticles(double amount) {
        return amount / getVolume();
    }

    /**
     * @param volume the volume, nonzero
     * @return the pressure if the container had a certain volume
     */
    default double getPressureForVolume(double volume) {
        return getTotalParticles() / volume;
    }

    /**
     * Change the amount of particles in the container by a given amount
     *
     * @param amount   the amount to change by
     * @param simulate whether to actually change the value or not
     * @return true if the change is safe, else false
     */
    default boolean changeTotalParticles(double amount, boolean simulate) { // TODO
        if (simulate) return isPressureSafe(getPressureForParticles(getTotalParticles() + amount));
        for (Fluid fluid : getParticleMap().keySet()) {
            setParticles(fluid, getParticles(fluid) + amount * getRatio(fluid));
        }
        return isPressureSafe();
    }

    /**
     * Change the amount of particles in the container by a given amount
     *
     * @param amount   the amount to change by
     * @param simulate whether to actually change the value or not
     * @return true if the change is safe, else false
     */
    default boolean changeParticles(Fluid fluid, double amount, boolean simulate) {
        if (simulate) return isPressureSafe(getPressureForParticles(getTotalParticles() + amount));
        setParticles(fluid, getParticles(fluid) + amount);
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
        cleanUp(); // TODO: move this elsewhere
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
