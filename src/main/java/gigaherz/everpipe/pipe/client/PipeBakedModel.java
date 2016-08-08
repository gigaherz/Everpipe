package gigaherz.everpipe.pipe.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.client.ModelHandle;
import gigaherz.everpipe.pipe.BlockPipe;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PipeBakedModel implements IBakedModel
{
    public static final ResourceLocation FAKE_LOCATION = Everpipe.location("models/block/custom/pipe");
    public static final ResourceLocation PIPE_CORE = Everpipe.location("block/pipe_core.obj");
    public static final ResourceLocation PIPE_SIDE = Everpipe.location("block/pipe_side.obj");

    private final ModelHandle handle_core = ModelHandle.of(PIPE_CORE);
    private final ModelHandle handle_side_u = ModelHandle.of(PIPE_SIDE).state("u", new TRSRTransformation(ModelRotation.getModelRotation(90, 0)));
    private final ModelHandle handle_side_d = ModelHandle.of(PIPE_SIDE).state("d", new TRSRTransformation(ModelRotation.getModelRotation(270, 0)));
    private final ModelHandle handle_side_e = ModelHandle.of(PIPE_SIDE).state("e", new TRSRTransformation(ModelRotation.getModelRotation(0, 270)));
    private final ModelHandle handle_side_w = ModelHandle.of(PIPE_SIDE).state("w", new TRSRTransformation(ModelRotation.getModelRotation(0, 90)));
    private final ModelHandle handle_side_n = ModelHandle.of(PIPE_SIDE).state("n", new TRSRTransformation(ModelRotation.getModelRotation(0, 180)));
    private final ModelHandle handle_side_s = ModelHandle.of(PIPE_SIDE);

    private final TextureAtlasSprite particle;

    public PipeBakedModel(TextureAtlasSprite particle)
    {
        this.particle = particle;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        List<BakedQuad> quads = Lists.newArrayList();

        quads.addAll(handle_core.get().getQuads(state, side, rand));
        if(state != null)
        {
            if (state.getValue(BlockPipe.UP)) quads.addAll(handle_side_u.get().getQuads(state, side, rand));
            if (state.getValue(BlockPipe.DOWN)) quads.addAll(handle_side_d.get().getQuads(state, side, rand));
            if (state.getValue(BlockPipe.EAST)) quads.addAll(handle_side_e.get().getQuads(state, side, rand));
            if (state.getValue(BlockPipe.WEST)) quads.addAll(handle_side_w.get().getQuads(state, side, rand));
            if (state.getValue(BlockPipe.NORTH)) quads.addAll(handle_side_n.get().getQuads(state, side, rand));
            if (state.getValue(BlockPipe.SOUTH)) quads.addAll(handle_side_s.get().getQuads(state, side, rand));
        }

        // TODO: Show connected interfaces

        return quads;
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return particle;
    }

    @Deprecated
    @Override
    public ItemCameraTransforms getItemCameraTransforms()
    {
        return null;
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return null;
    }

    public static class Model implements IModel, IRetexturableModel
    {
        @Nullable
        private final ResourceLocation particle;

        public Model()
        {
            this.particle = null;
        }

        public Model(String particle)
        {
            this.particle = particle == null ? null : new ResourceLocation(particle);
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            List<ResourceLocation> dependencies = Lists.newArrayList();
            dependencies.add(PIPE_CORE);
            dependencies.add(PIPE_SIDE);
            return dependencies;
        }

        @Override
        public Collection<ResourceLocation> getTextures()
        {
            if (particle != null)
                return Collections.singletonList(particle);
            return Collections.emptyList();
        }

        @Override
        public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter)
        {
            TextureAtlasSprite part = null;
            if (particle != null) part = bakedTextureGetter.apply(particle);
            return new PipeBakedModel(part);
        }

        @Override
        public IModelState getDefaultState()
        {
            return null;
        }

        @Override
        public IModel retexture(ImmutableMap<String, String> textures)
        {
            return new Model(textures.get("particle"));
        }
    }

    public static class ModelLoader implements ICustomModelLoader
    {

        @Override
        public boolean accepts(ResourceLocation modelLocation)
        {
            if(!modelLocation.getResourceDomain().equals(Everpipe.MODID))
                return false;
            return modelLocation.equals(FAKE_LOCATION);
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception
        {
            return new Model();
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager)
        {
            // Nothing to do
        }
    }

    public static class Statemapper extends StateMapperBase
    {
        public static final ModelResourceLocation LOCATION = new ModelResourceLocation(Everpipe.location("block_pipe"), "normal");

        @Override
        protected ModelResourceLocation getModelResourceLocation(IBlockState state)
        {
            return LOCATION;
        }
    }
}
