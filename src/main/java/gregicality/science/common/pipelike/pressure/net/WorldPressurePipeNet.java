package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregtech.api.pipenet.tickable.TickableWorldPipeNet;
import net.minecraft.world.World;

public class WorldPressurePipeNet extends TickableWorldPipeNet<PressurePipeProperties, PressurePipeNet> {
    public static int UPDATE_RATE = 8;
    private static final String DATA_ID = "gregtech.pressure_pipe_net";

    public WorldPressurePipeNet(String name) {
        super(name);
    }

    @Override
    protected int getUpdateRate() {
        return UPDATE_RATE;
    }

    public static WorldPressurePipeNet getWorldPipeNet(World world) {
        WorldPressurePipeNet netWorldData = (WorldPressurePipeNet) world.loadData(WorldPressurePipeNet.class, DATA_ID);
        if (netWorldData == null) {
            netWorldData = new WorldPressurePipeNet(DATA_ID);
            world.setData(DATA_ID, netWorldData);
        }
        netWorldData.setWorldAndInit(world);
        return netWorldData;
    }

    @Override
    protected PressurePipeNet createNetInstance() {
        return new PressurePipeNet(this);
    }
}
