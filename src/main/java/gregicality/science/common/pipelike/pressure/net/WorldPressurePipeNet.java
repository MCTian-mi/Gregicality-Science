package gregicality.science.common.pipelike.pressure.net;

import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregtech.api.pipenet.WorldPipeNet;
import net.minecraft.world.World;

public class WorldPressurePipeNet extends WorldPipeNet<PressurePipeProperties, PressurePipeNet> {

    private static final String DATA_ID = "gregtech.pressure_pipe_net";

    public WorldPressurePipeNet(String name) {
        super(name);
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
