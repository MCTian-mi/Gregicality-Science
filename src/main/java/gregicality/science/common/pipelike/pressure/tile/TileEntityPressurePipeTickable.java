package gregicality.science.common.pipelike.pressure.tile;

import net.minecraft.util.ITickable;

public class TileEntityPressurePipeTickable extends TileEntityPressurePipe implements ITickable {

    @Override
    public void update() {
        getCoverableImplementation().update();
    }

    @Override
    public boolean supportsTicking() {
        return true;
    }
}
