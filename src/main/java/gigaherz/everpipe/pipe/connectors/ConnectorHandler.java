package gigaherz.everpipe.pipe.connectors;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class ConnectorHandler extends IForgeRegistryEntry.Impl<ConnectorHandler>
{
    public static final IForgeRegistry<ConnectorHandler> REGISTRY = GameRegistry.findRegistry(ConnectorHandler.class);

    protected ConnectorHandler()
    {
    }

    public abstract Connector createInstance();

    public abstract ConnectorState createStateInstance();
}
