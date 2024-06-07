package gregicality.science.common.metatileentities.singleblock;

import codechicken.lib.raytracer.CuboidRayTraceResult;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.ColourMultiplier;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Matrix4;
import gregicality.science.api.GCYSValues;
import gregicality.science.api.capability.GCYSDataCodes;
import gregicality.science.api.capability.GCYSTileCapabilities;
import gregicality.science.api.capability.IPressureContainer;
import gregicality.science.api.capability.IPressureMachine;
import gregicality.science.api.capability.impl.PressureContainer;
import gregtech.api.GTValues;
import gregtech.api.capability.IActiveOutputSide;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.CycleButtonWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.PhantomFluidWidget;
import gregtech.api.gui.widgets.TextFieldWidget2;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.util.GTLog;
import gregtech.api.util.GTUtility;
import gregtech.client.renderer.texture.Textures;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.function.Function;

import static gregtech.api.capability.GregtechDataCodes.UPDATE_ACTIVE;
import static gregtech.api.unification.material.Materials.Air;

public class MetaTileEntityCreativePressurePump extends MetaTileEntity implements IPressureMachine, IActiveOutputSide {

    private PressureContainer pressureContainer;

    private static final int VOLUME = 640000;

    private boolean active = false;
    private FluidTank fluidTank;
    @Nullable
    private EnumFacing outputFacing;
    @Setter
    @Getter
    private double targetPressure = GCYSValues.EARTH_PRESSURE;

    public MetaTileEntityCreativePressurePump(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId);
        this.pressureContainer = new PressureContainer(this, Double.MIN_VALUE, Double.MAX_VALUE, VOLUME);
        this.fluidTank = new FluidTank(1);
//        this.fluidTank.fill(Air.getFluid(Integer.MAX_VALUE), true);
    }

    @Nonnull
    public static Function<String, String> getTextFieldValidator() {
        return val -> {
            if (val.isEmpty()) return "0";
            return val;
        };
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity iGregTechTileEntity) {
        return new MetaTileEntityCreativePressurePump(metaTileEntityId);
    }

    @Override
    protected void initializeInventory() {
        super.initializeInventory();
        this.pressureContainer = new PressureContainer(this, Double.MIN_VALUE, Double.MAX_VALUE, 1);
    }

    @Override
    public void renderMetaTileEntity(CCRenderState renderState, Matrix4 translation, IVertexOperation[] pipeline) {
        IVertexOperation[] renderPipeline = ArrayUtils.add(pipeline,
                new ColourMultiplier(GTUtility.convertRGBtoOpaqueRGBA_CL(getPaintingColorForRendering())));
        Textures.VOLTAGE_CASINGS[GTValues.MAX].render(renderState, translation, renderPipeline, Cuboid6.full);
        for (EnumFacing face : EnumFacing.VALUES) {
            Textures.INFINITE_EMITTER_FACE.renderSided(face, renderState, translation, pipeline); // TODO: Add new texture for this
        }
        Textures.PIPE_OUT_OVERLAY.renderSided(getOutputFacing(), renderState, translation, pipeline);
    }

    @Override
    public Pair<TextureAtlasSprite, Integer> getParticleTexture() {
        return Pair.of(Textures.VOLTAGE_CASINGS[GTValues.MAX].getParticleSprite(), getPaintingColorForRendering());
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing side) {
        if (capability == GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER) {
            if (side == null || side == getOutputFacing().getOpposite()) {
//            if (side == getOutputFacing().getOpposite()) {
                return GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER.cast(this.pressureContainer);
            }
            return null;
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected ModularUI createUI(EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.defaultBuilder()
                .bindPlayerInventory(entityPlayer.inventory);

        builder.label(7, 9, "gcys.creative.pressure.pump.gas"); // TODO make this accept gas only
        builder.widget(new PhantomFluidWidget(36, 6, 18, 18,
                () -> this.fluidTank.getFluid(), data -> {
            this.fluidTank.setFluid(data);
        }).showTip(false));

        builder.label(7, 32, "gcys.creative.pressure.pump.pressure");
        builder.widget(new ImageWidget(7, 45, 154, 14, GuiTextures.DISPLAY));
        builder.widget(new TextFieldWidget2(9, 47, 152, 16, () -> String.valueOf(getTargetPressure()), value -> {
            if (!value.isEmpty()) setTargetPressure(Double.parseDouble(value));
        }).setAllowedChars(TextFieldWidget2.DECIMALS).setMaxLength(35).setValidator(getTextFieldValidator()));

        builder.widget(new CycleButtonWidget(7, 62, 162, 20, () -> active, value -> active = value,
                "gregtech.creative.activity.off", "gregtech.creative.activity.on"));

        return builder.build(getHolder(), entityPlayer);
    }

    public Fluid getMarkedFluid() {
        return fluidTank.getFluid() == null ? Air.getFluid() : fluidTank.getFluid().getFluid();
    }

    @Override
    public void update() {
        super.update();
        if (getWorld().isRemote || !active) return; // TODO: make this tick less frequently

        double deltaPressure = targetPressure - pressureContainer.getPressure(getMarkedFluid());
        if (deltaPressure > 0) {
            pressureContainer.pushGas(getMarkedFluid(), deltaPressure * VOLUME, false);
        } else if (deltaPressure < 0) {
            pressureContainer.popGas(getMarkedFluid(), -deltaPressure * VOLUME, false);
        }

        TileEntity tile = getWorld().getTileEntity(getPos().offset(getOutputFacing()));
        if (tile != null) {
            IPressureContainer container = tile.getCapability(GCYSTileCapabilities.CAPABILITY_PRESSURE_CONTAINER, getOutputFacing().getOpposite());
            if (container == null) return;
            if (container.getPressure() != GCYSValues.EARTH_PRESSURE &&
                    (container.getPressure() == container.getMinPressure() ||
                            container.getPressure() == container.getMaxPressure())) return;
            IPressureContainer.mergeContainers(this.pressureContainer, container);
        }
    }

    @Override
    public boolean onWrenchClick(EntityPlayer playerIn, EnumHand hand, EnumFacing facing,
                                 CuboidRayTraceResult hitResult) {
        if (!playerIn.isSneaking()) {
            if (getOutputFacing() == facing) {
                return false;
            }
            if (!getWorld().isRemote) {
                setOutputFacing(facing);
            }
            return true;
        }
        return super.onWrenchClick(playerIn, hand, facing, hitResult);
    }

    @Override
    public IPressureContainer getPressureContainer() {
        return pressureContainer;
    }

    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound data) {
        super.writeToNBT(data);
        data.setTag("PressureContainer", this.pressureContainer.serializeNBT());
        data.setBoolean("Active", active);
        data.setInteger("OutputFacing", getOutputFacing().getIndex());
        data.setTag("FluidInventory", fluidTank.writeToNBT(new NBTTagCompound()));
        data.setDouble("TargetPressure", targetPressure);
        return data;
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound data) {
        super.readFromNBT(data);
        this.pressureContainer.deserializeNBT(data.getCompoundTag("PressureContainer"));
        this.active = data.getBoolean("Active");
        this.outputFacing = EnumFacing.VALUES[data.getInteger("OutputFacing")];
        this.fluidTank.readFromNBT(data.getCompoundTag("FluidInventory"));
        this.targetPressure = data.getDouble("TargetPressure");
    }

    public EnumFacing getOutputFacing() {
        return this.outputFacing == null ? EnumFacing.SOUTH : this.outputFacing;
    }

    @Override
    public void writeInitialSyncData(@Nonnull PacketBuffer buf) {
        super.writeInitialSyncData(buf);
        buf.writeInt(getOutputFacing().getIndex());
        buf.writeCompoundTag(this.pressureContainer.serializeNBT());
        buf.writeBoolean(this.active);
        buf.writeCompoundTag(this.fluidTank.writeToNBT(new NBTTagCompound()));
        buf.writeDouble(this.targetPressure);
    }

    @Override
    public void receiveInitialSyncData(@Nonnull PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.outputFacing = EnumFacing.VALUES[buf.readInt()];

        try {
            NBTTagCompound compound = buf.readCompoundTag();
            if (compound != null) this.pressureContainer.deserializeNBT(compound);
        } catch (Exception ignored) {}

        this.active = buf.readBoolean();

        try {
            this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(buf.readCompoundTag()));
        } catch (IOException e) {
            GTLog.logger.warn("Failed to load fluid from NBT in a creative pressure pump at " + this.getPos() +
                    " on initial server/client sync");
        }

        this.targetPressure = buf.readDouble();
    }

    @Override
    public void receiveCustomData(int dataId, @Nonnull PacketBuffer buf) {
        super.receiveCustomData(dataId, buf);
        if (dataId == GCYSDataCodes.UPDATE_PUMPING_SIDE) {
            this.outputFacing = EnumFacing.VALUES[buf.readByte()];
            scheduleRenderUpdate();
//        } else if (dataId == UPDATE_FLUID) {
//            try {
//                this.fluidTank.setFluid(FluidStack.loadFluidStackFromNBT(buf.readCompoundTag()));
//            } catch (IOException e) {
//                GTLog.logger.warn("Failed to load fluid from NBT in a creative pressure pump at {} on a routine fluid update", this.getPos());
//            }
        } else if (dataId == UPDATE_ACTIVE) {
            this.active = buf.readBoolean();
        }
    }

    public void setOutputFacing(@Nonnull EnumFacing outputFacing) {
        this.outputFacing = outputFacing;
        if (!this.getWorld().isRemote) {
            this.markDirty();
            writeCustomData(GCYSDataCodes.UPDATE_PUMPING_SIDE, buf -> buf.writeByte(this.getOutputFacing().getIndex()));
        }
    }

    @Override
    public boolean isAutoOutputItems() {
        return false;
    }

    @Override
    public boolean isAutoOutputFluids() {
        return false;
    }

    @Override
    public boolean isAllowInputFromOutputSideItems() {
        return false;
    }

    @Override
    public boolean isAllowInputFromOutputSideFluids() {
        return false;
    }
}
