package gregicality.science.client.render.pipe;

import codechicken.lib.render.CCRenderState;
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
import static gregicality.science.common.pipelike.pressure.BlockPressurePipe.getFlangeBox;
import static gregtech.api.unification.material.Materials.Paper;

public class PressurePipeRenderer extends PipeRenderer {

    public static final PressurePipeRenderer INSTANCE = new PressurePipeRenderer();
    private final EnumMap<PressurePipeType, TextureAtlasSprite> pipeTextures = new EnumMap<>(PressurePipeType.class);
    private final EnumMap<PressurePipeType, TextureAtlasSprite> pipeTexturesPaper = new EnumMap<>(PressurePipeType.class);

    private PressurePipeRenderer() {
        super("gcys_pressure_pipe", gcysId("pressure_pipe"));
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

        pipeTexturesPaper.put(PressurePipeType.TINY, GCYSTextures.PIPE_PRESSURE_TINY_PAPER);
        pipeTexturesPaper.put(PressurePipeType.SMALL, GCYSTextures.PIPE_PRESSURE_SMALL_PAPER);
        pipeTexturesPaper.put(PressurePipeType.NORMAL, GCYSTextures.PIPE_PRESSURE_NORMAL_PAPER);
        pipeTexturesPaper.put(PressurePipeType.LARGE, GCYSTextures.PIPE_PRESSURE_LARGE_PAPER);

        pipeTexturesPaper.put(PressurePipeType.SEALED_TINY, GCYSTextures.PIPE_PRESSURE_TINY_PAPER);
        pipeTexturesPaper.put(PressurePipeType.SEALED_SMALL, GCYSTextures.PIPE_PRESSURE_SMALL_PAPER);
        pipeTexturesPaper.put(PressurePipeType.SEALED_NORMAL, GCYSTextures.PIPE_PRESSURE_NORMAL_PAPER);
        pipeTexturesPaper.put(PressurePipeType.SEALED_LARGE, GCYSTextures.PIPE_PRESSURE_LARGE_PAPER);

    }

    @Override
    public void buildRenderer(PipeRenderContext renderContext, BlockPipe<?, ?, ?> blockPipe, IPipeTile<?, ?> pipeTile,
                              IPipeType<?> pipeType, @Nullable Material material) {
        if (material == null || !(pipeType instanceof PressurePipeType)) {
            return;
        }
        if (material == Paper) {
            renderContext.addOpenFaceRender(new IconTransformation(pipeTexturesPaper.get(pipeType)))
                    .addSideRender(new IconTransformation(GCYSTextures.PIPE_PRESSURE_SIDE_PAPER));
        } else {
            renderContext.addOpenFaceRender(new IconTransformation(pipeTextures.get(pipeType)))
                    .addSideRender(new IconTransformation(GCYSTextures.PIPE_PRESSURE_SIDE));
        }

        if (((PressurePipeType) pipeType).isSealed()) {
            renderContext.addOpenFaceRender(new IconTransformation(GCYSTextures.SEALED_OVERLAY));
        }
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IPipeType<?> iPipeType, @Nullable Material material) {
        return GCYSTextures.PIPE_PRESSURE_SIDE;
    }

    protected void renderFlange(CCRenderState renderState, PipeRenderContext renderContext, EnumFacing side) {
        Cuboid6 cuboid = getFlangeBox(side, renderContext.getPipeThickness());
        for (EnumFacing renderedSide : EnumFacing.VALUES) {
            renderOpenFace(renderState, renderContext, renderedSide, cuboid);
        }
    }

    @Override
    public void renderPipeCube(CCRenderState renderState, PipeRenderContext renderContext, EnumFacing side) {
        super.renderPipeCube(renderState, renderContext, side);
        if ((renderContext.getConnections() & 1 << (6 + side.getIndex())) > 0) return;
        renderFlange(renderState, renderContext, side);
    }
}
