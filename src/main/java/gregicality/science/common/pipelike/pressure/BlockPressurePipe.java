package gregicality.science.common.pipelike.pressure;

import com.google.common.base.Preconditions;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.unification.materials.properties.PressurePipeProperties;
import gregicality.science.client.render.pipe.PressurePipeRenderer;
import gregicality.science.common.pipelike.pressure.net.WorldPressurePipeNet;
import gregicality.science.common.pipelike.pressure.tile.TileEntityPressurePipe;
import gregtech.api.GregTechAPI;
import gregtech.api.items.toolitem.ToolClasses;
import gregtech.api.pipenet.block.material.BlockMaterialPipe;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.pipenet.tile.TileEntityPipeBase;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.registry.MaterialRegistry;
import gregtech.api.util.TaskScheduler;
import gregtech.client.renderer.pipe.PipeRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class BlockPressurePipe extends BlockMaterialPipe<PressurePipeType, PressurePipeProperties, WorldPressurePipeNet> {

    private final SortedMap<Material, PressurePipeProperties> enabledMaterials = new TreeMap<>();

    public BlockPressurePipe(PressurePipeType pressurePipeType, MaterialRegistry registry) {
        super(pressurePipeType, registry);
        setCreativeTab(GregTechAPI.TAB_GREGTECH_PIPES);
        setHarvestLevel(ToolClasses.WRENCH, 1);
    }

    public void addPipeMaterial(Material material, PressurePipeProperties properties) {
        Preconditions.checkNotNull(material, "material");
        Preconditions.checkNotNull(properties, "material %s pressurePipeProperties was null", material);
        Preconditions.checkArgument(material.getRegistry().getNameForObject(material) != null,
                "material %s is not registered", material);
        this.enabledMaterials.put(material, properties);
    }

    public Collection<Material> getEnabledMaterials() {
        return Collections.unmodifiableSet(enabledMaterials.keySet());
    }

    @Override
    public Class<PressurePipeType> getPipeTypeClass() {
        return PressurePipeType.class;
    }

    @Override
    public WorldPressurePipeNet getWorldPipeNet(World world) {
        return WorldPressurePipeNet.getWorldPipeNet(world);
    }

    @Override
    protected PressurePipeProperties createProperties(PressurePipeType pipeType, Material material) {
        return pipeType.modifyProperties(enabledMaterials.getOrDefault(material, getFallbackType()));
    }

    @SideOnly(Side.CLIENT)
    @NotNull
    @Override
    public PipeRenderer getPipeRenderer() {
        return PressurePipeRenderer.INSTANCE;
    }

    @Override
    protected PressurePipeProperties getFallbackType() {
        return enabledMaterials.values().iterator().next();
    }

    @Override
    public TileEntityPipeBase<PressurePipeType, PressurePipeProperties> createNewTileEntity(boolean b) {
        return new TileEntityPressurePipe();
    }

    @Override
    public void getSubBlocks(@NotNull CreativeTabs itemIn, @NotNull NonNullList<ItemStack> items) {
        for (Material material : enabledMaterials.keySet()) {
            items.add(getItem(material));
        }
    }

    @Override
    public void breakBlock(@NotNull World worldIn, @NotNull BlockPos pos, @NotNull IBlockState state) {
        // TODO: Leak particles
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityPressurePipe) {
            TaskScheduler.scheduleTask(worldIn, ((TileEntityPressurePipe) te)::updateLeakage);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean canPipesConnect(IPipeTile<PressurePipeType, PressurePipeProperties> selfTile, EnumFacing side,
                                   IPipeTile<PressurePipeType, PressurePipeProperties> sideTile) {
        return selfTile instanceof TileEntityPressurePipe && sideTile instanceof TileEntityPressurePipe;
    }

    @Override
    public boolean canPipeConnectToBlock(IPipeTile<PressurePipeType, PressurePipeProperties> selfTile, EnumFacing side,
                                         TileEntity tile) {
        return tile != null &&
                tile.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, side.getOpposite()) != null;
    }

    @Override
    public boolean isHoldingPipe(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack stack = player.getHeldItemMainhand();
        return stack != ItemStack.EMPTY && stack.getItem() instanceof ItemBlockPressurePipe;
    }

    @Override
    @NotNull
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(@NotNull IBlockState state) {
        return PressurePipeRenderer.INSTANCE.getBlockRenderType();
    }

    @Override
    protected Pair<TextureAtlasSprite, Integer> getParticleTexture(World world, BlockPos blockPos) {
        return PressurePipeRenderer.INSTANCE.getParticleTexture((TileEntityPressurePipe) world.getTileEntity(blockPos));
    }
}
