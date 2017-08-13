package gigaherz.everpipe;

import gigaherz.common.BlockRegistered;
import gigaherz.everpipe.common.ConfigValues;
import gigaherz.everpipe.common.GuiHandler;
import gigaherz.everpipe.common.IModProxy;
import gigaherz.everpipe.network.UpdateField;
import gigaherz.everpipe.pipe.BlockPipe;
import gigaherz.everpipe.pipe.TilePipe;
import gigaherz.everpipe.pipe.connectors.ConnectorHandler;
import gigaherz.everpipe.pipe.connectors.items.ItemHandlerConnector;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
@Mod(modid = Everpipe.MODID,
        version = Everpipe.VERSION)
public class Everpipe
{
    public static final String MODID = "everpipe";
    public static final String VERSION = "@VERSION@";
    public static final String CHANNEL = "everpipe";

    public static BlockRegistered pipe;

    public static CreativeTabs tabEverpipe = new CreativeTabs("tab_everpipe")
    {
        @Override
        public ItemStack getTabIconItem()
        {
            return new ItemStack(pipe);
        }
    };

    @Mod.Instance(value = MODID)
    public static Everpipe instance;

    @SidedProxy(clientSide = "gigaherz.everpipe.client.ClientProxy", serverSide = "gigaherz.everpipe.server.ServerProxy")
    public static IModProxy proxy;

    public static SimpleNetworkWrapper channel;

    private GuiHandler guiHandler = new GuiHandler();

    public static final Logger logger = LogManager.getLogger(MODID);

    public static final IForgeRegistry<ConnectorHandler> CONNECTOR_HANDLERS;

    static
    {
        CONNECTOR_HANDLERS = new RegistryBuilder<ConnectorHandler>()
                .setType(ConnectorHandler.class)
                .setIDRange(0, 65535)
                .setName(Everpipe.location("connector_handler_registry"))
                .create();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(
                pipe = new BlockPipe("block_pipe")
        );

        GameRegistry.registerTileEntity(TilePipe.class, pipe.getRegistryName().toString());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(
                pipe.createItemBlock()
        );
    }

    @SubscribeEvent
    public static void registerConnectors(RegistryEvent.Register<ConnectorHandler> event)
    {
        event.getRegistry().registerAll(
                new ItemHandlerConnector.Handler()
        );
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        ConfigValues.readConfig(config);

        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        int messageNumber = 0;
        channel.registerMessage(UpdateField.Handler.class, UpdateField.class, messageNumber++, Side.CLIENT);
        logger.debug("Final message number: " + messageNumber);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Recipes
        /*GameRegistry.addRecipe(new ItemStack(riftOrb),
                "aba",
                "bcb",
                "aba",
                'a', Items.MAGMA_CREAM,
                'b', Items.ENDER_PEARL,
                'c', Items.ENDER_EYE);*/

        //GameRegistry.addRecipe(new RecipeRiftDuplication());
        //RecipeSorter.register(MODID + ":rift_duplication", RecipeRiftDuplication.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);

        //FMLInterModComms.sendMessage("waila", "register", "gigaherz.enderRift.integration.WailaProviders.callbackRegister");
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
