package gregicality.science.api.capability.impl;

import static org.junit.jupiter.api.Assertions.*;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.junit.jupiter.api.Test;

class GasMapTest {

    @Test
    void test() {
        GasMap gasMap = new GasMap();

        ResourceLocation dummy = new ResourceLocation("minecraft:dummy");
        Fluid steam = new Fluid("steam", dummy, dummy);
        Fluid hydrogen = new Fluid("hydrogen", dummy, dummy);
        Fluid oxygen = new Fluid("oxygen", dummy, dummy);

        gasMap.pushGas(steam, 1000);
        gasMap.pushGas(hydrogen, 2000);

        assertEquals(3000, gasMap.getTotalGasAmount());
        assertEquals(1.0/3.0, gasMap.getRatio(steam));
        assertEquals(2.0/3.0, gasMap.getRatio(hydrogen));

        gasMap.pushGas(oxygen, 10000.0/GasMap.MIN_RATIO);

        gasMap.cleanUp();
        assertEquals(0, gasMap.getGasAmount(steam));
        assertEquals(0, gasMap.getGasAmount(hydrogen));
    }
}
