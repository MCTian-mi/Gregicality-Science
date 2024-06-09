package gregicality.science.common.metatileentities.singleblock;

import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.IPressureMachine;
import gregicality.science.api.capability.impl.PressureContainer;
import gregicality.science.api.capability.impl.SimplePressureRecipeLogic;
import gregicality.science.api.recipes.GCYSRecipeMaps;
import gregtech.api.capability.impl.RecipeLogicEnergy;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.SimpleMachineMetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.recipes.RecipeMap;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.Textures;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import java.io.IOException;

public class MetaTileEntityGCYSAutoclave extends SimpleMachineMetaTileEntity implements IPressureMachine {

    private IPressureContainer pressureContainer;

    public MetaTileEntityGCYSAutoclave(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap, ICubeRenderer renderer, int tier) {
        super(metaTileEntityId, recipeMap, renderer, tier, false);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity tileEntity) {
        return new MetaTileEntityGCYSAutoclave(this.metaTileEntityId, GCYSRecipeMaps.GCYS_AUTOCLAVE_RECIPES,
                Textures.AUTOCLAVE_OVERLAY, this.getTier());
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        if (pressureContainer == null) {
//            this.pressureContainer = new PressureContainer(this, GCYSValues.EARTH_PRESSURE, 100 * GCYSValues.EARTH_PRESSURE, 16000);
            this.pressureContainer = new PressureContainer(this, Double.MIN_VALUE, Double.MAX_VALUE, 16000);
        }
    }

    @Override
    protected RecipeLogicEnergy createWorkable(RecipeMap<?> recipeMap) {
        return new SimplePressureRecipeLogic(this, recipeMap, () -> energyContainer, () -> pressureContainer);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
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
        data.setTag("PressureContainer", pressureContainer.serializeNBT());
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
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
}
