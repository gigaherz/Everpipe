package gigaherz.everpipe.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.IModelState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.util.Map;

public class ModelHandle
{
    static Map<String, IBakedModel> loadedModels = Maps.newHashMap();

    private ResourceLocation model;
    private String key;
    private final Map<String, String> textureReplacements = Maps.newHashMap();
    private VertexFormat vertexFormat = DefaultVertexFormats.ITEM;
    private IModelState state;

    private ModelHandle(ResourceLocation model)
    {
        this.model = model;
        this.key = model.toString();
    }

    public ModelHandle replace(String texChannel, String resloc)
    {
        key += "//" + texChannel + "/" + resloc;
        textureReplacements.put(texChannel, resloc);
        return this;
    }

    public ModelHandle vertexFormat(VertexFormat fmt)
    {
        key += "//VF:" + fmt.hashCode();
        vertexFormat = fmt;
        return this;
    }

    public ModelHandle state(String statekey, IModelState newState)
    {
        key += "//state/" + statekey;
        state = newState;
        return this;
    }

    public ResourceLocation getModel()
    {
        return model;
    }

    public String getKey()
    {
        return key;
    }

    public Map<String, String> getTextureReplacements()
    {
        return textureReplacements;
    }

    public VertexFormat getVertexFormat()
    {
        return vertexFormat;
    }

    public IModelState getState()
    {
        return state;
    }

    public IBakedModel get()
    {
        return loadModel(this);
    }

    public void render() { renderModel(get(), getVertexFormat()); }
    public void render(int color) { renderModel(get(), getVertexFormat(), color); }

    // ========================================================= STATIC METHODS

    public static void init()
    {
        IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
        if (rm instanceof IReloadableResourceManager)
        {
            ((IReloadableResourceManager) rm).registerReloadListener(new IResourceManagerReloadListener()
            {
                @Override
                public void onResourceManagerReload(IResourceManager __)
                {
                    loadedModels.clear();
                }
            });
        }
    }

    @Nonnull
    public static ModelHandle of(String model)
    {
        return new ModelHandle(new ResourceLocation(model));
    }

    @Nonnull
    public static ModelHandle of(ResourceLocation model)
    {
        return new ModelHandle(model);
    }

    private static void renderModel(IBakedModel model, VertexFormat fmt)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, fmt);
        for (BakedQuad bakedquad : model.getQuads(null, null, 0))
        {
            worldrenderer.addVertexData(bakedquad.getVertexData());
        }
        tessellator.draw();
    }

    private static void renderModel(IBakedModel model, VertexFormat fmt, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer worldrenderer = tessellator.getBuffer();
        worldrenderer.begin(GL11.GL_QUADS, fmt);
        for (BakedQuad bakedquad : model.getQuads(null, null, 0))
        {
            LightUtil.renderQuadColor(worldrenderer, bakedquad, color);
        }
        tessellator.draw();
    }

    private static IBakedModel loadModel(ModelHandle handle)
    {
        IBakedModel model = loadedModels.get(handle.getKey());
        if (model != null)
            return model;

        try
        {
            IModel mod = ModelLoaderRegistry.getModel(handle.getModel());
            if (mod instanceof IRetexturableModel && handle.getTextureReplacements().size() > 0)
            {
                IRetexturableModel rtm = (IRetexturableModel) mod;
                mod = rtm.retexture(ImmutableMap.copyOf(handle.getTextureReplacements()));
            }
            IModelState state = handle.getState();
            if (state == null) state = mod.getDefaultState();
            model = mod.bake(state, handle.getVertexFormat(), ModelLoader.defaultTextureGetter());
            loadedModels.put(handle.getKey(), model);
            return model;
        }
        catch (Exception e)
        {
            throw new ReportedException(new CrashReport("Error loading custom model " + handle.getModel(), e));
        }
    }
}
