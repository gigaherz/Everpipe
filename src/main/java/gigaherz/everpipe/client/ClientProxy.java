package gigaherz.everpipe.client;

import gigaherz.common.client.ModelHandle;
import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.common.ContainerBase;
import gigaherz.everpipe.common.IModProxy;
import gigaherz.everpipe.network.UpdateField;
import gigaherz.everpipe.pipe.client.PipeBakedModel;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;

public class ClientProxy implements IModProxy
{
    @Override
    public void preInit()
    {
        OBJLoader.INSTANCE.addDomain(Everpipe.MODID);

        ModelHandle.init();

        ModelLoaderRegistry.registerLoader(new PipeBakedModel.ModelLoader());
        ModelLoader.setCustomStateMapper(Everpipe.pipe, new PipeBakedModel.Statemapper());

        registerBlockModelAsItem(Everpipe.pipe);
    }

    public void registerBlockModelAsItem(final Block block)
    {
        registerBlockModelAsItem(block, 0, "inventory");
    }

    public void registerBlockModelAsItem(final Block block, int meta, final String variant)
    {
        Item item = Item.getItemFromBlock(block);
        registerItemModel(item, meta, variant);
    }

    public void registerItemModel(final Item item, int meta, final String variantName)
    {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), variantName));
    }

    @Override
    public void init()
    {
    }

    @Override
    public void handleUpdateField(final UpdateField message)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> ClientProxy.this.handleUpdateField_internal(message));
    }

    void handleUpdateField_internal(UpdateField message)
    {
        Minecraft gameController = Minecraft.getMinecraft();

        EntityPlayer entityplayer = gameController.player;

        if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == message.windowId)
        {
            ((ContainerBase) entityplayer.openContainer).updateFields(message.fields);
        }
    }
}
