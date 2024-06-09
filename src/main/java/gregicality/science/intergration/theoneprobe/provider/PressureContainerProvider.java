package gregicality.science.intergration.theoneprobe.provider;

import gregicality.GCYSInternalTags;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.utils.NumberFormattingUtil;
import gregtech.api.util.TextComponentUtil;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PressureContainerProvider implements IProbeInfoProvider {

    @Override
    public String getID() {
        return GCYSInternalTags.MODID + ":pressure_container_provider";
    }

    @Override
    public void addProbeInfo(@NotNull ProbeMode mode, @NotNull IProbeInfo probeInfo, @NotNull EntityPlayer player,
                             @NotNull World world, @NotNull IBlockState blockState, @NotNull IProbeHitData data) {
        if (blockState.getBlock().hasTileEntity(blockState)) {
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity != null && tileEntity.hasCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, null)) {
                IPressureContainer pressureContainer = tileEntity.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, null);
                if (pressureContainer != null) {

                    // Total Pressure
                    ITextComponent componentPressure = TextComponentUtil.translationWithColor(TextFormatting.GRAY, NumberFormattingUtil.formatDoubleToCompactString(pressureContainer.getPressure()));
                    probeInfo.text(TextComponentUtil.translationWithColor(TextFormatting.WHITE,
                            "gcys.top.pressure",
                            componentPressure).getFormattedText());

                    // Partial Pressure
                    List<String> partialPressureStrings = new ArrayList<>();
                    pressureContainer.getGasMap().forEach((fluid, amount) -> {
                        ITextComponent componentPartialPressure = TextComponentUtil.translationWithColor(TextFormatting.GRAY, NumberFormattingUtil.formatDoubleToCompactString(pressureContainer.getPressure(fluid)));
                        partialPressureStrings.add(TextComponentUtil.translationWithColor(TextFormatting.WHITE,
                                "gcys.top.partial_pressure",
                                fluid.getLocalizedName(new FluidStack(fluid, 1)),
                                componentPartialPressure).getFormattedText());
                    });
                    partialPressureStrings.forEach(probeInfo::text);
                }
            }
        }
    }
}
