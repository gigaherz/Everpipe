package gigaherz.everpipe.pipe;

import com.google.common.collect.*;
import gigaherz.everpipe.pipe.connectors.Connector;
import gigaherz.everpipe.pipe.connectors.ConnectorStateData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Map;

public class TilePipe extends TileEntity
{
    private final Multimap<EnumFacing, Connector> connectors = ArrayListMultimap.create();

    public ConnectorStateData getConnectors()
    {
        return new ConnectorStateData(connectors);
    }

    public boolean addConnector(EnumFacing side, Connector connector)
    {
        if (connectors.get(side).size() >= 9)
            return false;
        connectors.put(side, connector);
        return true;
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
            Connector conn = new Connector();
            conn.deserializeNBT(tag.getCompoundTag("Data"));
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
            NBTTagCompound tag = new NBTTagCompound();

            tag.setString("Side", entry.getKey().getName());
            tag.setTag("Data", entry.getValue().serializeNBT());

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

}
