package gigaherz.everpipe.pipe.connectors;

import gigaherz.everpipe.Everpipe;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

public abstract class ConnectorHandler extends IForgeRegistryEntry.Impl<ConnectorHandler>
{
    public static final IForgeRegistry<ConnectorHandler> REGISTRY = new RegistryBuilder<ConnectorHandler>()
            .setType(ConnectorHandler.class)
            .setIDRange(0, 65535)
            .setName(Everpipe.location("connector_handler_registry"))
            .create();

    protected ConnectorHandler()
    {
    }

    public abstract Connector createInstance();
}
