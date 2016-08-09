package gigaherz.everpipe.pipe.connectors.power;

import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.pipe.connectors.Connector;
import gigaherz.everpipe.pipe.connectors.ConnectorHandler;
import gigaherz.everpipe.pipe.connectors.ConnectorState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TeslaConnector extends Connector
{
    public static final ResourceLocation KEY = Everpipe.location("tesla");

    protected TeslaConnector(ConnectorHandler connectorType)
    {
        super(connectorType);
    }

    @Override
    public ConnectorState immutableCopy()
    {
        return new State(getConnectorHandler(), this);
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return super.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        super.deserializeNBT(nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return false;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return null;
    }

    public static class State extends ConnectorState
    {
        // TODO: add item handler values for display

        public State(ConnectorHandler handler)
        {
            super(handler);
        }

        public State(State copyFrom)
        {
            super(copyFrom.getConnectorHandler());
        }

        public State(ConnectorHandler connectorHandler, TeslaConnector copyFrom)
        {
            super(connectorHandler);
        }

        @Override
        public ConnectorState immutableCopy()
        {
            return new State(this);
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound)
        {

        }
    }

    public static class Handler extends ConnectorHandler
    {
        public Handler()
        {
            setRegistryName(KEY);
        }

        @Override
        public Connector createInstance()
        {
            return new TeslaConnector(this);
        }

        @Override
        public ConnectorState createStateInstance()
        {
            return new State(this);
        }
    }
}
