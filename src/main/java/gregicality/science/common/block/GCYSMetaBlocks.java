package gregicality.science.common.block;

import gregicality.science.client.render.pipe.PressurePipeRenderer;
import gregicality.science.common.block.blocks.BlockCrucible;
import gregicality.science.common.block.blocks.BlockGCYSMultiblockCasing;
import gregicality.science.common.block.blocks.BlockGCYSMultiblockCasingActive;
import gregicality.science.common.block.blocks.BlockTransparentCasing;
import gregicality.science.common.pipelike.pressure.BlockPressurePipe;
import gregicality.science.common.pipelike.pressure.PressurePipeType;
import gregtech.api.GregTechAPI;
import gregtech.api.unification.OreDictUnifier;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.registry.MaterialRegistry;
import gregtech.client.model.SimpleStateMapper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GCYSMetaBlocks {

    public static final Map<String, BlockPressurePipe[]> PRESSURE_PIPES = new Object2ObjectOpenHashMap<>();
    public static BlockCrucible CRUCIBLE;
    public static BlockGCYSMultiblockCasing MULTIBLOCK_CASING;
    public static BlockGCYSMultiblockCasingActive MULTIBLOCK_CASING_ACTIVE;
    public static BlockTransparentCasing TRANSPARENT_CASING;

    private GCYSMetaBlocks() {
    }

    public static void init() {
        CRUCIBLE = new BlockCrucible();
        CRUCIBLE.setRegistryName("crucible");
        MULTIBLOCK_CASING = new BlockGCYSMultiblockCasing();
        MULTIBLOCK_CASING.setRegistryName("multiblock_casing");
        MULTIBLOCK_CASING_ACTIVE = new BlockGCYSMultiblockCasingActive();
        MULTIBLOCK_CASING_ACTIVE.setRegistryName("multiblock_casing_active");
        TRANSPARENT_CASING = new BlockTransparentCasing();
        TRANSPARENT_CASING.setRegistryName("transparent_casing");

        for (MaterialRegistry registry : GregTechAPI.materialManager.getRegistries()) {
            String modid = registry.getModid();
            BlockPressurePipe[] pressurePipes = new BlockPressurePipe[PressurePipeType.VALUES.length];

            for (PressurePipeType type : PressurePipeType.VALUES) {
                pressurePipes[type.ordinal()] = new BlockPressurePipe(type, registry);
                pressurePipes[type.ordinal()].setRegistryName(modid, String.format("pressure_pipe_%s", type.name));
            }
            PRESSURE_PIPES.put(modid, pressurePipes);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(CRUCIBLE);
        registerItemModel(MULTIBLOCK_CASING);
        registerItemModel(MULTIBLOCK_CASING_ACTIVE);
        registerItemModel(TRANSPARENT_CASING);

        for (MaterialRegistry registry : GregTechAPI.materialManager.getRegistries()) {
            for (BlockPressurePipe pipe : PRESSURE_PIPES.get(registry.getModid())) pipe.onModelRegister();
        }
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(@Nonnull Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            //noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            statePropertiesToString(state.getProperties())));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerStateMappers() {
        IStateMapper normalStateMapper;
        for (MaterialRegistry registry : GregTechAPI.materialManager.getRegistries()) {
            normalStateMapper = new SimpleStateMapper(PressurePipeRenderer.INSTANCE.getModelLocation());
            for (BlockPressurePipe pipe : PRESSURE_PIPES.get(registry.getModid())) {
                ModelLoader.setCustomStateMapper(pipe, normalStateMapper);
            }
        }
    }

    @Nonnull
    private static String statePropertiesToString(@Nonnull Map<IProperty<?>, Comparable<?>> properties) {
        StringBuilder stringbuilder = new StringBuilder();
        List<Map.Entry<IProperty<?>, Comparable<?>>> entries = properties.entrySet().stream().sorted(Comparator.comparing((c) -> c.getKey().getName())).collect(Collectors.toList());

        for (Map.Entry<IProperty<?>, Comparable<?>> iPropertyComparableEntry : entries) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty<?> property = (IProperty<?>) ((Map.Entry<?, ?>) iPropertyComparableEntry).getKey();
            stringbuilder.append(property.getName());
            stringbuilder.append("=");
            stringbuilder.append(getPropertyName(property, (Comparable<?>) ((Map.Entry<?, ?>) iPropertyComparableEntry).getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    public static void registerOreDict() {
        for (MaterialRegistry registry : GregTechAPI.materialManager.getRegistries()) {
            for (BlockPressurePipe pipe : PRESSURE_PIPES.get(registry.getModid())) {
                for (Material pipeMaterial : pipe.getEnabledMaterials()) {
                    ItemStack itemStack = pipe.getItem(pipeMaterial);
                    OreDictUnifier.registerOre(itemStack, pipe.getPrefix(), pipeMaterial);
                }
            }
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getPropertyName(@Nonnull IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}
