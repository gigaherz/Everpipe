package gigaherz.everpipe.pipe.connectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public abstract class Connector implements ICapabilitySerializable<NBTTagCompound>
{
    private final ConnectorHandler connectorHandler;

    protected Connector(ConnectorHandler connectorType)
    {
        this.connectorHandler = connectorType;
    }

    public abstract ConnectorState getImmutableState();

    public ConnectorHandler getConnectorHandler()
    {
        return connectorHandler;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Handler", connectorHandler.getRegistryName().toString());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {

    }
}
