package gregicality.science.api.unification.materials.properties;

import gregicality.science.api.GCYSValues;
import gregtech.api.unification.material.properties.IMaterialProperty;
import gregtech.api.unification.material.properties.MaterialProperties;
import gregtech.api.unification.material.properties.PropertyKey;
import lombok.Getter;

import java.util.Objects;

@Getter
public class PressurePipeProperties implements IMaterialProperty {

    public final double maxPressure;
    private final double minPressure;
    private final int volume;
    private final float pressureTightness;

    public PressurePipeProperties(double minPressure, double maxPressure, int volume, float pressureTightness) {
        this.minPressure = minPressure;
        this.maxPressure = maxPressure;
        this.volume = volume;
        this.pressureTightness = pressureTightness;
    }

    public PressurePipeProperties() {
        this(GCYSValues.EARTH_PRESSURE, GCYSValues.EARTH_PRESSURE, 1, 1f);
    }

    @Override
    public void verifyProperty(MaterialProperties properties) {
        properties.ensureSet(PropertyKey.INGOT, true);

        if (properties.hasProperty(PropertyKey.ITEM_PIPE) || properties.hasProperty(PropertyKey.FLUID_PIPE)) { // TODO: decide whether we really want this
            throw new IllegalStateException(
                    "Material " + properties.getMaterial() +
                            " has both Pressure and Fluid (or Item) Pipe Property, which is not allowed!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PressurePipeProperties other)) return false;
        return getMaxPressure() == other.getMaxPressure() &&
                getMinPressure() == other.getMinPressure() &&
                getVolume() == other.getVolume() &&
                getPressureTightness() == other.getPressureTightness();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaxPressure(), getMinPressure(), getVolume(), getPressureTightness());
    }

    @Override
    public String toString() {
        return "PressurePipeProperties" +
                "{maxPressure=" + maxPressure +
                ", minPressure=" + minPressure +
                ", volume=" + volume +
                ", pressureTightness=" + pressureTightness +
                '}';
    }
}
