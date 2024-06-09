package gregicality.science.common.pipelike.pressure;

import gregicality.science.api.GCYSValues;
import gregtech.api.pipenet.block.IPipeType;

import javax.annotation.Nonnull;

public enum PressurePipeType implements IPipeType<PressurePipeData> {
    LOW_VACUUM("low_vacuum", 0.125f, GCYSValues.P[GCYSValues.LV], GCYSValues.EARTH_PRESSURE, 10000),
    MEDIUM_VACUUM("medium_vacuum", 0.25f, GCYSValues.P[GCYSValues.MV], GCYSValues.EARTH_PRESSURE, 12500),
    HIGH_VACUUM("high_vacuum", 0.5f, GCYSValues.P[GCYSValues.HV], GCYSValues.EARTH_PRESSURE, 15000),
    ULTRA_HIGH_VACUUM("ultra_high_vacuum", 0.625f, GCYSValues.P[GCYSValues.UHV], GCYSValues.EARTH_PRESSURE, 20000),
    EXTREMELY_HIGH_VACUUM("extremely_high_vacuum", 0.75f, GCYSValues.P[GCYSValues.EHV], GCYSValues.EARTH_PRESSURE, 30000),
    CLOSE_SPACE_VACUUM("close_space_vacuum", 0.825f, GCYSValues.P[GCYSValues.CSV], GCYSValues.EARTH_PRESSURE, 50000),
    INTERGALACTIC_VOID_VACUUM("intergalactic_void_vacuum", 0.9f, GCYSValues.P[GCYSValues.IVV], GCYSValues.EARTH_PRESSURE, 100000),
    LOW_PRESSURE("low_pressure", 0.75f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.LP], 10000),
    MEDIUM_PRESSURE("medium_pressure", 0.625f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.MP], 7500),
    HIGH_PRESSURE("high_pressure", 0.5f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.HP], 6250),
    ULTRA_HIGH_PRESSURE("ultra_high_pressure", 0.375f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.UHP], 5000),
    ELECTRON_DEGENERACY_PRESSURE("electron_degeneracy_pressure", 0.25f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.EDP], 250),
    WHITE_DWARF_PRESSURE("white_dwarf_pressure", 0.125f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.WDP], 125),
    NEUTRON_STAR_PRESSURE("neutron_star_pressure", 0.05f, GCYSValues.EARTH_PRESSURE, GCYSValues.P[GCYSValues.NSP], 10);

    public final float thickness;
    public final String name;
    public final double maxPressure;
    private final double minPressure;
    private final int volume;

    PressurePipeType(String name, float thickness, double minPressure, double maxPressure, int volume) {
        this.thickness = thickness;
        this.name = name;
        this.minPressure = minPressure;
        this.maxPressure = maxPressure;
        this.volume = volume;
    }

    @Override
    public float getThickness() {
        return this.thickness;
    }

    public double getMinPressure() {
        return this.minPressure;
    }

    public double getMaxPressure() {
        return this.maxPressure;
    }

    public int getVolume() {
        return this.volume;
    }

    @Nonnull
    @Override
    public PressurePipeData modifyProperties(PressurePipeData pipeData) {
        return new PressurePipeData(minPressure, maxPressure, volume);
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
}
