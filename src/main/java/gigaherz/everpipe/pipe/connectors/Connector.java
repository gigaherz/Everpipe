package gigaherz.everpipe.pipe.connectors;

import gigaherz.everpipe.pipe.channels.ChannelType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public abstract class Connector extends ConnectorState implements ICapabilitySerializable<NBTTagCompound>
{
    public ChannelType channelType;

    protected Connector(ConnectorHandler handler, ChannelType channelType)
    {
        super(handler);
        this.channelType = channelType;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString("Handler", getConnectorHandler().getRegistryName().toString());
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {

    }
}
