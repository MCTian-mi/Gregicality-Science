package gregicality.science.common.pipelike.pressure;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.api.utils.GCYSUtility;
import gregicality.science.api.utils.NumberFormattingUtil;
import gregtech.api.pipenet.block.ItemBlockPipe;
import gregtech.api.pipenet.block.material.BlockMaterialPipe;
import gregtech.client.utils.TooltipHelper;
import gregtech.common.ConfigHolder;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBlockPressurePipe extends ItemBlockPipe<PressurePipeType, PressurePipeProperties> {

    public ItemBlockPressurePipe(BlockPressurePipe block) {
        super(block);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn,
                               @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        PressurePipeProperties pipeProperties = this.blockPipe.createItemProperties(stack);
        tooltip.add(I18n.format("gcys.universal.tooltip.volume", pipeProperties.getVolume()));
        tooltip.add(I18n.format("gcys.universal.tooltip.min_pressure", NumberFormattingUtil.formatDoubleToCompactString(pipeProperties.getMinPressure()), GCYSValues.PNF[GCYSUtility.getTierByPressure(pipeProperties.getMinPressure())]));
        tooltip.add(I18n.format("gcys.universal.tooltip.max_pressure", NumberFormattingUtil.formatDoubleToCompactString(pipeProperties.getMaxPressure()), GCYSValues.PNF[GCYSUtility.getTierByPressure(pipeProperties.getMaxPressure())]));

        if (TooltipHelper.isShiftDown()) {
            tooltip.add(I18n.format("gregtech.tool_action.wrench.connect"));
            tooltip.add(I18n.format("gregtech.tool_action.screwdriver.access_covers"));
            tooltip.add(I18n.format("gregtech.tool_action.crowbar"));
        } else {
            tooltip.add(I18n.format("gregtech.tool_action.show_tooltips"));
        }

        if (ConfigHolder.misc.debug) {
            BlockMaterialPipe<?, ?, ?> blockMaterialPipe = (BlockMaterialPipe<?, ?, ?>) blockPipe;
            tooltip.add("MetaItem Id: " + blockMaterialPipe.getPrefix().name +
                    blockMaterialPipe.getItemMaterial(stack).toCamelCaseString());
        }
    }
}
