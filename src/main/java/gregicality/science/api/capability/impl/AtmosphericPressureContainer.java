package gregicality.science.api.capability.impl;

import gregicality.science.api.GCYSValues;
import gregtech.api.metatileentity.MetaTileEntity;

public class AtmosphericPressureContainer extends PressureContainer { // why this exists?

    /**
     * Atmopsheric pressure container which always remains at atmospheric
     *
     * @param volume the volume of the container, must be nonzero
     */
    public AtmosphericPressureContainer(MetaTileEntity metaTileEntity, int volume) {
        super(metaTileEntity, GCYSValues.EARTH_PRESSURE * 0.9, GCYSValues.EARTH_PRESSURE * 1.1, volume);
    }
}
