package gregicality.science.api.capability;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.utils.GCYSUtility;
import gregicality.science.api.utils.NumberFormattingUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface IPressureMachine {

    /**
     * @return the pressure container of this machine
     */
    IPressureContainer getPressureContainer();

    World getWorld();

    BlockPos getPos();

    default void balancePressure() {
        ArrayList<IPressureContainer> containers = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity tile = getWorld().getTileEntity(getPos().offset(facing));
            if (tile != null) {
                IPressureContainer container = tile.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, facing.getOpposite());
                if (container != null) {
                    containers.add(container);
                }
            }
        }
        if (containers.isEmpty()) return;
        containers.add(this.getPressureContainer());
        IPressureContainer.mergeContainers(false, containers.toArray(new IPressureContainer[0]));
        if (!getPressureContainer().isPressureSafe())
            getPressureContainer().causePressureExplosion(getWorld(), getPos()); // TODO
    }

    default void addPressureInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        tooltip.add(I18n.format("gcys.universal.tooltip.min_pressure", new Object[]{NumberFormattingUtil.formatDoubleToCompactString(getPressureContainer().getMinPressure()), GCYSValues.PNF[GCYSUtility.getTierByPressure(getPressureContainer().getMinPressure())]}));
        tooltip.add(I18n.format("gcys.universal.tooltip.max_pressure", new Object[]{NumberFormattingUtil.formatDoubleToCompactString(getPressureContainer().getMaxPressure()), GCYSValues.PNF[GCYSUtility.getTierByPressure(getPressureContainer().getMaxPressure())]}));
        tooltip.add(I18n.format("gcys.universal.tooltip.volume", new Object[]{getPressureContainer().getVolume()}));
    }
}
