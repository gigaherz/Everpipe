package gigaherz.everpipe.pipe;

import com.google.common.collect.*;
import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.pipe.connectors.Connector;
import gigaherz.everpipe.pipe.connectors.ConnectorHandler;
import gigaherz.everpipe.pipe.connectors.ConnectorStateData;
import gigaherz.everpipe.pipe.connectors.items.ItemHandlerConnector;
import gigaherz.graph.api.Graph;
import gigaherz.graph.api.GraphObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TilePipe extends TileEntity implements ITickable, GraphObject
{
    private Graph graph;
    private boolean firstUpdate = true;

    @Override
    public Graph getGraph()
    {
        return graph;
    }

    @Override
    public void setGraph(Graph graph)
    {
        this.graph = graph;
    }

    private final Multimap<EnumFacing, Connector> connectors = ArrayListMultimap.create();

    public ConnectorStateData getConnectors()
    {
        return new ConnectorStateData(connectors);
    }

    public boolean addConnector(EnumFacing side, Connector connector)
    {
        Collection<Connector> bySide = connectors.get(side);
        if (bySide.size() >= 9)
            return false;
        if (bySide.stream().anyMatch(c -> c.getConnectorHandler() == connector.getConnectorHandler()))
            return false;
        connectors.put(side, connector);
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if(facing != null)
        {
            return connectors.get(facing).stream()
                    .anyMatch(c -> c.hasCapability(capability, facing));
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if(facing != null)
        {
            return connectors.get(facing).stream()
                    .filter(c -> c.hasCapability(capability, facing))
                    .findFirst().get()
                    .getCapability(capability, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        connectors.clear();
        NBTTagList list = compound.getTagList("Connectors", Constants.NBT.TAG_COMPOUND);
        for(int i=0;i<list.tagCount();i++)
        {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            EnumFacing side = EnumFacing.byName(tag.getString("Side"));

            if (!tag.hasKey("Handler", Constants.NBT.TAG_STRING))
            {
                Everpipe.logger.warn("Ignored connector missing handler key.");
                continue;
            }
            ResourceLocation key = new ResourceLocation(tag.getString("Handler"));
            ConnectorHandler handler = ConnectorHandler.REGISTRY.getValue(key);
            if (handler == null)
            {
                Everpipe.logger.warn("Ignored unregistered connector handler: {0}", key);
                continue;
            }
            Connector conn = handler.createInstance();
            conn.deserializeNBT(tag);
            connectors.put(side,conn);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);

        NBTTagList list = new NBTTagList();

        for(Map.Entry<EnumFacing, Connector> entry : connectors.entries())
        {
            NBTTagCompound tag = entry.getValue().serializeNBT();

            tag.setString("Side", entry.getKey().getName());

            list.appendTag(tag);
        }

        compound.setTag("Connectors", list);
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void validate()
    {
        super.validate();

        if (!firstUpdate)
            init();
    }

    @Override
    public void update()
    {
        if (firstUpdate)
        {
            firstUpdate = false;
            init();
        }
    }

    private void init()
    {
        Graph.integrate(this, getNeighbours());
        //updateConnectedInventories();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();

        Graph graph = this.getGraph();
        if (graph != null)
            graph.remove(this);
    }

    private List<GraphObject> getNeighbours()
    {
        List<GraphObject> neighbours = Lists.newArrayList();
        for (EnumFacing f : EnumFacing.VALUES)
        {
            TileEntity teOther = worldObj.getTileEntity(pos.offset(f));
            if (!(teOther instanceof TilePipe))
                continue;
            GraphObject thingOther = ((TilePipe) teOther);
            if (thingOther.getGraph() != null)
                neighbours.add(thingOther);
        }
        return neighbours;
    }

    public void updateNeighbours()
    {
        Graph graph = this.getGraph();
        if (graph != null)
        {
            graph.addNeighours(this, getNeighbours());
        }

        //updateConnectedInventories();
    }

    public void broadcastDirty()
    {
        if (getGraph() == null)
            return;

        for (GraphObject object : getGraph().getObjects())
        {
            if (!(object instanceof TilePipe))
                continue;
            TilePipe proxy = (TilePipe) object;

            if (!proxy.isInvalid())
                proxy.markDirty();
        }
    }
}
