package gregicality.science.common;

import gregicality.GCYSInternalTags;
import gregicality.science.api.unification.materials.properties.GCYSPropertyKey;
import gregicality.science.common.block.GCYSMetaBlocks;
import gregicality.science.common.items.GCYSMetaItems;
import gregicality.science.common.pipelike.pressure.BlockPressurePipe;
import gregicality.science.common.pipelike.pressure.ItemBlockPressurePipe;
import gregicality.science.common.pipelike.pressure.tile.TileEntityPressurePipe;
import gregicality.science.loaders.recipe.GCYSMaterialInfoLoader;
import gregicality.science.loaders.recipe.GCYSRecipeLoader;
import gregicality.science.loaders.recipe.component.GCYSCraftingComponent;
import gregtech.api.GregTechAPI;
import gregtech.api.block.VariantItemBlock;
import gregtech.api.unification.material.Material;
import gregtech.api.unification.material.registry.MaterialRegistry;
import gregtech.api.unification.stack.ItemMaterialInfo;
import gregtech.loaders.recipe.CraftingComponent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;

import static gregicality.science.common.block.GCYSMetaBlocks.PRESSURE_PIPES;
import static gregicality.science.common.metatileentities.GCYSMetaTileEntities.gcysId;

@Mod.EventBusSubscriber(modid = GCYSInternalTags.MODID)
public class CommonProxy {

    @SubscribeEvent
    public static void syncConfigValues(@Nonnull ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(GCYSInternalTags.MODID)) {
            ConfigManager.sync(GCYSInternalTags.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(@Nonnull RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.register(GCYSMetaBlocks.CRUCIBLE);
        registry.register(GCYSMetaBlocks.MULTIBLOCK_CASING);
        registry.register(GCYSMetaBlocks.MULTIBLOCK_CASING_ACTIVE);
        registry.register(GCYSMetaBlocks.TRANSPARENT_CASING);

        for (MaterialRegistry materialRegistry : GregTechAPI.materialManager.getRegistries()) {
            for (Material material : materialRegistry) {
                if (material.hasProperty(GCYSPropertyKey.PRESSURE_PIPE)) {
                    for (BlockPressurePipe pipe : PRESSURE_PIPES.get(materialRegistry.getModid())) {
                        if (!pipe.getItemPipeType(pipe.getItem(material)).getOrePrefix().isIgnored(material)) {
                            pipe.addPipeMaterial(material, material.getProperty(GCYSPropertyKey.PRESSURE_PIPE));
                        }
                    }
                }
            }
            for (BlockPressurePipe pipe : PRESSURE_PIPES.get(materialRegistry.getModid())) registry.register(pipe);
        }
    }

    @SubscribeEvent
    public static void registerItems(@Nonnull RegistryEvent.Register<Item> event) {
        GCYSMetaItems.initSubitems();

        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(createItemBlock(GCYSMetaBlocks.CRUCIBLE, VariantItemBlock::new));
        registry.register(createItemBlock(GCYSMetaBlocks.MULTIBLOCK_CASING, VariantItemBlock::new));
        registry.register(createItemBlock(GCYSMetaBlocks.MULTIBLOCK_CASING_ACTIVE, VariantItemBlock::new));
        registry.register(createItemBlock(GCYSMetaBlocks.TRANSPARENT_CASING, VariantItemBlock::new));

        for (MaterialRegistry materialRegistry : GregTechAPI.materialManager.getRegistries()) {
            for (BlockPressurePipe pipe : PRESSURE_PIPES.get(materialRegistry.getModid()))
                registry.register(createItemBlock(pipe, ItemBlockPressurePipe::new));
        }
    }

    @Nonnull
    private static <T extends Block> ItemBlock createItemBlock(@Nonnull T block, @Nonnull Function<T, ItemBlock> producer) {
        ItemBlock itemBlock = producer.apply(block);
        itemBlock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
        return itemBlock;
    }

    // using low to ensure some recipes from CEu are removed
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        // Main recipe registration
        // This is called AFTER GregTech registers recipes, so
        // anything here is safe to call removals in
        GCYSRecipeLoader.init();
    }

    @SubscribeEvent()
    public static void registerRecipesNormal(RegistryEvent.Register<IRecipe> event) {
        // Main recipe registration
        // This is called AFTER GregTech registers recipes, so
        // anything here is safe to call removals in
        GCYSRecipeLoader.initHandlers();
    }

    @SubscribeEvent
    public static void initMaterialInfo(GregTechAPI.RegisterEvent<ItemMaterialInfo> event) {
        GCYSMaterialInfoLoader.init();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void initComponents(GregTechAPI.RegisterEvent<CraftingComponent> event) {
        GCYSCraftingComponent.init();
    }

    public void preLoad() {
        GameRegistry.registerTileEntity(TileEntityPressurePipe.class, gcysId("pressure_pipe"));
    }
}
