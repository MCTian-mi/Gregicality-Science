package gregicality.science.client.render.pipe;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.pipeline.IVertexOperation;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.uv.IconTransformation;
import gregicality.science.client.render.GCYSTextures;
import gregicality.science.common.pipelike.pressure.PressurePipeType;
import gregtech.api.pipenet.block.BlockPipe;
import gregtech.api.pipenet.block.IPipeType;
import gregtech.api.pipenet.tile.IPipeTile;
import gregtech.api.unification.material.Material;
import gregtech.client.renderer.pipe.PipeRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.EnumMap;

import static gregicality.science.common.metatileentities.GCYSMetaTileEntities.gcysId;

public class PressurePipeRenderer extends PipeRenderer {

    public static final PressurePipeRenderer INSTANCE = new PressurePipeRenderer();
    private final EnumMap<PressurePipeType, TextureAtlasSprite> pipeTextures = new EnumMap<>(PressurePipeType.class);

    private PressurePipeRenderer() {
        super("gcys_pressure_pipe", gcysId("pressure_pipe"));
    }

    public static Cuboid6 getFlangeBox(EnumFacing side, float thickness) {
        float min = (0.75F - thickness) / 2.0F + 0.01F;
        float max = 1.0F - min;
        float faceMin = 0.0001F;
        float faceMax = 0.9999F;
        float flangeMin = 0.125F;
        float flangeMax = 0.875F;

        if (side == null) {
            return new Cuboid6((double) min, (double) min, (double) min, (double) max, (double) max, (double) max);
        } else {
            return switch (side) {
                case WEST ->
                        new Cuboid6((double) faceMin, (double) min, (double) min, (double) flangeMin, (double) max, (double) max);
                case EAST ->
                        new Cuboid6((double) flangeMax, (double) min, (double) min, (double) faceMax, (double) max, (double) max);
                case NORTH ->
                        new Cuboid6((double) min, (double) min, (double) faceMin, (double) max, (double) max, (double) flangeMin);
                case SOUTH ->
                        new Cuboid6((double) min, (double) min, (double) flangeMax, (double) max, (double) max, (double) faceMax);
                case UP ->
                        new Cuboid6((double) min, (double) flangeMax, (double) min, (double) max, (double) faceMax, (double) max);
                case DOWN ->
                        new Cuboid6((double) min, (double) faceMin, (double) min, (double) max, (double) flangeMin, (double) max);
            };
        }
    }

    @Override
    public void registerIcons(TextureMap map) {
        pipeTextures.put(PressurePipeType.TINY, GCYSTextures.PIPE_PRESSURE_TINY);
        pipeTextures.put(PressurePipeType.SMALL, GCYSTextures.PIPE_PRESSURE_SMALL);
        pipeTextures.put(PressurePipeType.NORMAL, GCYSTextures.PIPE_PRESSURE_NORMAL);
        pipeTextures.put(PressurePipeType.LARGE, GCYSTextures.PIPE_PRESSURE_LARGE);

        pipeTextures.put(PressurePipeType.SEALED_TINY, GCYSTextures.PIPE_PRESSURE_TINY);
        pipeTextures.put(PressurePipeType.SEALED_SMALL, GCYSTextures.PIPE_PRESSURE_SMALL);
        pipeTextures.put(PressurePipeType.SEALED_NORMAL, GCYSTextures.PIPE_PRESSURE_NORMAL);
        pipeTextures.put(PressurePipeType.SEALED_LARGE, GCYSTextures.PIPE_PRESSURE_LARGE);
    }

    @Override
    public void buildRenderer(PipeRenderContext renderContext, BlockPipe<?, ?, ?> blockPipe, IPipeTile<?, ?> pipeTile,
                              IPipeType<?> pipeType, @Nullable Material material) {
        if (material == null || !(pipeType instanceof PressurePipeType)) {
            return;
        }
        renderContext.addOpenFaceRender(new IconTransformation(pipeTextures.get(pipeType)))
                .addSideRender(new IconTransformation(GCYSTextures.PIPE_PRESSURE_SIDE));

        if (((PressurePipeType) pipeType).isSealed()) {
            renderContext.addSideRender(false, new IconTransformation(GCYSTextures.SEALED_OVERLAY));
        }
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IPipeType<?> iPipeType, @Nullable Material material) {
        return GCYSTextures.PIPE_PRESSURE_SIDE;
    }

    protected void renderFlange(CCRenderState renderState, PipeRenderContext renderContext, EnumFacing side) {
        for (EnumFacing renderedSide : EnumFacing.VALUES) {
            for (IVertexOperation[] operations : renderContext.getOpenFaceRenderer()) {
                renderFace(renderState, operations, renderedSide, getFlangeBox(side, renderContext.getPipeThickness()));
            }
        }
    }

    @Override
    public void renderPipeCube(CCRenderState renderState, PipeRenderContext renderContext, EnumFacing side) {
        super.renderPipeCube(renderState, renderContext, side);
        if ((renderContext.getConnections() & 1 << (6 + side.getIndex())) <= 0) {
            renderFlange(renderState, renderContext, side);
        }
    }
}
