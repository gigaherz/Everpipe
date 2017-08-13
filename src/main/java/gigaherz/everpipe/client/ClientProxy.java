package gigaherz.everpipe.client;

import gigaherz.common.client.ModelHandle;
import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.common.ContainerBase;
import gigaherz.everpipe.common.IModProxy;
import gigaherz.everpipe.network.UpdateField;
import gigaherz.everpipe.pipe.client.PipeBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static gigaherz.common.client.ModelHelpers.registerBlockModelAsItem;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IModProxy
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        OBJLoader.INSTANCE.addDomain(Everpipe.MODID);

        ModelHandle.init();

        ModelLoaderRegistry.registerLoader(new PipeBakedModel.ModelLoader());
        ModelLoader.setCustomStateMapper(Everpipe.pipe, new PipeBakedModel.Statemapper());

        registerBlockModelAsItem(Everpipe.pipe);
    }

    @Override
    public void handleUpdateField(final UpdateField message)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft gameController = Minecraft.getMinecraft();

            EntityPlayer entityplayer = gameController.player;

            if (entityplayer.openContainer != null && entityplayer.openContainer.windowId == message.windowId)
            {
                ((ContainerBase) entityplayer.openContainer).updateFields(message.fields);
            }
        });
    }
}
