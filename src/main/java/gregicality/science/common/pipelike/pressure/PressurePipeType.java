package gregicality.science.common.pipelike.pressure;

import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.api.unification.ore.GCYSOrePrefix;
import gregtech.api.pipenet.block.material.IMaterialPipeType;
import gregtech.api.unification.ore.OrePrefix;
import lombok.Getter;

import javax.annotation.Nonnull;

public enum PressurePipeType implements IMaterialPipeType<PressurePipeProperties> {

    TINY("tiny", 0.25f, GCYSOrePrefix.pipeTinyPressure, 1, 1),
    SMALL("small", 0.375f, GCYSOrePrefix.pipeSmallPressure, 2, 1),
    NORMAL("normal", 0.5f, GCYSOrePrefix.pipeNormalPressure, 6, 1),
    LARGE("large", 0.75f, GCYSOrePrefix.pipeLargePressure, 12, 1),

    SEALED_TINY("tiny_sealed", 0.25f, GCYSOrePrefix.pipeTinyPressureSealed, 1, 4),
    SEALED_SMALL("small_sealed", 0.375f, GCYSOrePrefix.pipeSmallPressureSealed, 2, 4),
    SEALED_NORMAL("normal_sealed", 0.5f, GCYSOrePrefix.pipeNormalPressureSealed, 6, 4),
    SEALED_LARGE("large_sealed", 0.75f, GCYSOrePrefix.pipeLargePressureSealed, 12, 4);

    public static final PressurePipeType[] VALUES = values();

    public final String name;
    public final float thickness;
    @Getter
    private final int volumeMultiplier;
    @Getter
    private final OrePrefix orePrefix;
    @Getter
    private final int pressureTightnessMultiplier;

    PressurePipeType(String name, float thickness, OrePrefix orePrefix, int volumeMultiplier, int pressureTightnessMultiplier) {
        this.thickness = thickness;
        this.name = name;
        this.orePrefix = orePrefix;
        this.volumeMultiplier = volumeMultiplier;
        this.pressureTightnessMultiplier = pressureTightnessMultiplier;
    }

    @Override
    public float getThickness() {
        return this.thickness;
    }

    @Override
    public PressurePipeProperties modifyProperties(PressurePipeProperties baseProperties) {
        return new PressurePipeProperties(
                baseProperties.getMinPressure(),
                baseProperties.getMaxPressure(),
                baseProperties.getVolume() * volumeMultiplier,
                baseProperties.getPressureTightness() * pressureTightnessMultiplier);
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
