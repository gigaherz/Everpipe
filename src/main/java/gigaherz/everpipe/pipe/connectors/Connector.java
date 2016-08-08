package gigaherz.everpipe.pipe.connectors;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public class Connector implements INBTSerializable<NBTTagCompound>
{
    public ConnectorState getImmutableState()
    {
        return new ConnectorState();
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return new NBTTagCompound();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {

    }
}
