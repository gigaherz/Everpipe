package gigaherz.everpipe;

import gigaherz.capabilities.api.energy.CapabilityEnergy;
import gigaherz.common.BlockRegistered;
import gigaherz.everpipe.common.ConfigValues;
import gigaherz.everpipe.common.GuiHandler;
import gigaherz.everpipe.common.IModProxy;
import gigaherz.everpipe.network.UpdateField;
import gigaherz.everpipe.pipe.BlockPipe;
import gigaherz.everpipe.pipe.TilePipe;
import gigaherz.everpipe.pipe.connectors.items.ItemHandlerConnector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Everpipe.MODID,
     version = Everpipe.VERSION)
public class Everpipe
{
    public static final String MODID = "everpipe";
    public static final String VERSION = "@VERSION@";
    public static final String CHANNEL = "everpipe";

    public static BlockRegistered pipe;

    public static CreativeTabs tabEverpipe = new CreativeTabs("tabEverpipe")
    {
        @Override
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(pipe);
        }
    };

    @Mod.Instance(value = MODID)
    public static Everpipe instance;

    @SidedProxy(clientSide = "gigaherz.everpipe.client.ClientProxy", serverSide = "gigaherz.everpipe.server.ServerProxy")
    public static IModProxy proxy;

    public static SimpleNetworkWrapper channel;

    private GuiHandler guiHandler = new GuiHandler();

    public static final Logger logger = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        ConfigValues.readConfig(config);

        CapabilityEnergy.enable();

        pipe = new BlockPipe("block_pipe");
        GameRegistry.register(pipe);
        GameRegistry.register(pipe.createItemBlock());
        GameRegistry.registerTileEntity(TilePipe.class, "tileEverpipe");

        GameRegistry.register(new ItemHandlerConnector.Handler());

        channel = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        int messageNumber = 0;
        channel.registerMessage(UpdateField.Handler.class, UpdateField.class, messageNumber++, Side.CLIENT);
        logger.debug("Final message number: " + messageNumber);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();

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

        FMLInterModComms.sendMessage("Waila", "register", "gigaherz.enderRift.integration.WailaProviders.callbackRegister");
    }

    public static ResourceLocation location(String path)
    {
        return new ResourceLocation(MODID, path);
    }
}
