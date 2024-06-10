package gregicality.science.common.metatileentities.singleblock;

import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.IPressureMachine;
import gregicality.science.api.capability.impl.PressureContainer;
import gregicality.science.api.capability.impl.SimplePressureRecipeLogic;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.client.particle.IMachineParticleEffect;
import gregtech.client.renderer.ICubeRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

public class SimplePressureMetaTileEntity extends SimpleMachineMetaTileEntity implements IPressureMachine {

    private final IPressureContainer pressureContainer;
    private final Function<Integer, Integer> gasTankScalingFunction;
    private final Function<Integer, Double> minimumPressureFunction;
    private final Function<Integer, Double> maximumPressureFunction;

    private int pressureTicks = 0;

    public SimplePressureMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                        ICubeRenderer renderer, int tier, boolean isVacuumContainer) {
        this(metaTileEntityId, recipeMap, renderer, tier, isVacuumContainer, false);
    }

    public SimplePressureMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                        ICubeRenderer renderer, int tier, boolean isVacuumContainer, boolean hasFrontFacing) {
        this(metaTileEntityId, recipeMap, renderer, tier, isVacuumContainer, hasFrontFacing, GTUtility.defaultTankSizeFunction);
    }

    public SimplePressureMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                        ICubeRenderer renderer, int tier, boolean isVacuumContainer, boolean hasFrontFacing,
                                        Function<Integer, Integer> tankScalingFunction) {
        this(metaTileEntityId, recipeMap, renderer, tier, hasFrontFacing, tankScalingFunction, GTUtility.defaultTankSizeFunction,
                isVacuumContainer ? GCYSValues.defaultMinimumPressureFunction : (intTier) -> GCYSValues.EARTH_PRESSURE,
                isVacuumContainer ? (intTier) -> GCYSValues.EARTH_PRESSURE : GCYSValues.defaultMaximumPressureFunction,
                null, null);
    }

    public SimplePressureMetaTileEntity(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap,
                                        ICubeRenderer renderer, int tier, boolean hasFrontFacing,
                                        Function<Integer, Integer> tankScalingFunction,
                                        Function<Integer, Integer> gasTankScalingFunction,
                                        Function<Integer, Double> minimumPressureFunction,
                                        Function<Integer, Double> maximumPressureFunction,
                                        @Nullable IMachineParticleEffect tickingParticle,
                                        @Nullable IMachineParticleEffect randomParticle) {
        super(metaTileEntityId, recipeMap, renderer, tier, hasFrontFacing, tankScalingFunction, tickingParticle, randomParticle);
        this.gasTankScalingFunction = gasTankScalingFunction;
        this.minimumPressureFunction = minimumPressureFunction;
        this.maximumPressureFunction = maximumPressureFunction;
        this.pressureContainer = createPressureContainer();
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new SimplePressureMetaTileEntity(this.metaTileEntityId, workable.getRecipeMap(), renderer, getTier(),
                hasFrontFacing(), getTankScalingFunction(), gasTankScalingFunction,
                minimumPressureFunction, maximumPressureFunction, tickingParticle, randomParticle);
    }

    protected IPressureContainer createPressureContainer() {
        return new PressureContainer(this, minimumPressureFunction.apply(getTier()), maximumPressureFunction.apply(getTier()), gasTankScalingFunction.apply(this.getTier()));
    }

    @Override
    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        return new SimplePressureRecipeLogic(this, recipeMap, () -> energyContainer, () -> pressureContainer);
    }

    @Override
    public void update() {
        pressureTicks = (pressureTicks + 1) % 20;
        if (!getWorld().isRemote && pressureTicks == 0) {
            balancePressure();
        }
        super.update();
    }

    @Override
    public IPressureContainer getPressureContainer() {
        return pressureContainer;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability.equals(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER)) {
            return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(pressureContainer);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setInteger("PressureTicks", pressureTicks);
        data.setTag("PressureContainer", pressureContainer.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        if (data.hasKey("PressureTicks")) {
            pressureTicks = data.getInteger("PressureTicks");
        }
        if (data.hasKey("PressureContainer")) {
            this.pressureContainer.deserializeNBT(data.getCompoundTag("PressureContainer"));
        }
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeCompoundTag(pressureContainer.serializeNBT());
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        try {
            this.pressureContainer.deserializeNBT(buf.readCompoundTag());
        } catch (IOException ignored) {
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        addPressureInformation(stack, player, tooltip, advanced);
    }
}
