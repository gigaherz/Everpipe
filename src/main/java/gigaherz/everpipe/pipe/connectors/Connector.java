package gigaherz.everpipe.pipe.connectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public abstract class Connector extends ConnectorState implements ICapabilitySerializable<NBTTagCompound>
{
    protected Connector(ConnectorHandler handler)
    {
        super(handler);
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
