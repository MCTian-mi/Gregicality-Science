package gregicality.science.common.pipelike.pressure;

import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.api.unification.ore.GCYSOrePrefix;
import gregtech.api.pipenet.block.material.IMaterialPipeType;
import gregtech.api.unification.ore.OrePrefix;
import lombok.Getter;

import javax.annotation.Nonnull;

public enum PressurePipeType implements IMaterialPipeType<PressurePipeProperties> {

    TINY("tiny", 0.25f, GCYSOrePrefix.pipeTinyPressure, 1, 0),
    SMALL("small", 0.375f, GCYSOrePrefix.pipeSmallPressure, 2, 0),
    NORMAL("normal", 0.5f, GCYSOrePrefix.pipeNormalPressure, 6, 0),
    LARGE("large", 0.75f, GCYSOrePrefix.pipeLargePressure, 12, 0),

    SEALED_TINY("tiny_sealed", 0.25f, GCYSOrePrefix.pipeTinyPressureSealed, 1, 0.2),
    SEALED_SMALL("small_sealed", 0.375f, GCYSOrePrefix.pipeSmallPressureSealed, 2, 0.2),
    SEALED_NORMAL("normal_sealed", 0.5f, GCYSOrePrefix.pipeNormalPressureSealed, 6, 0.2),
    SEALED_LARGE("large_sealed", 0.75f, GCYSOrePrefix.pipeLargePressureSealed, 12, 0.2);

    public static final PressurePipeType[] VALUES = values();

    public final String name;
    public final float thickness;
    @Getter
    private final int volumeMultiplier;
    @Getter
    private final double bonus;
    @Getter
    private final OrePrefix orePrefix;

    PressurePipeType(String name, float thickness, OrePrefix orePrefix, int volumeMultiplier, double bonus) {
        this.thickness = thickness;
        this.name = name;
        this.orePrefix = orePrefix;
        this.volumeMultiplier = volumeMultiplier;
        this.bonus = bonus;
    }

    @Override
    public float getThickness() {
        return this.thickness;
    }

    @Override
    public PressurePipeProperties modifyProperties(PressurePipeProperties baseProperties) {
        return new PressurePipeProperties(
                baseProperties.getMinPressure() * (1 - bonus),
                baseProperties.getMaxPressure() * (1 + bonus),
                baseProperties.getVolume() * volumeMultiplier);
    }


    @Override
    public boolean isPaintable() {
        return true;
    }

    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    public boolean isSealed() {
        return ordinal() > 3;
    }
}
