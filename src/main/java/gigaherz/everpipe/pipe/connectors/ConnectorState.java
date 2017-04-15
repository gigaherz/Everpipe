package gigaherz.everpipe.pipe.connectors;


import net.minecraft.nbt.NBTTagCompound;

public abstract class ConnectorState
{
    private final ConnectorHandler connectorHandler;

    protected ConnectorState(ConnectorHandler handler)
    {
        this.connectorHandler = handler;
    }

    public ConnectorHandler getConnectorHandler()
    {
        return connectorHandler;
    }

    public abstract ConnectorState immutableCopy();

    public abstract void deserializeNBT(NBTTagCompound compound);
}
