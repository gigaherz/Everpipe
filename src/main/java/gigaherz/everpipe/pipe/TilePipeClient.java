package gigaherz.everpipe.pipe;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gigaherz.everpipe.Everpipe;
import gigaherz.everpipe.pipe.connectors.ConnectorHandler;
import gigaherz.everpipe.pipe.connectors.ConnectorState;
import gigaherz.everpipe.pipe.connectors.ConnectorStateData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class TilePipeClient extends TileEntity
{
    private final Multimap<EnumFacing, ConnectorState> connectors = ArrayListMultimap.create();

    public ConnectorStateData getConnectors()
    {
        return new ConnectorStateData(connectors);
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
            ConnectorState conn = handler.createStateInstance();
            conn.deserializeNBT(tag);
            connectors.put(side,conn);
        }
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        handleUpdateTag(pkt.getNbtCompound());

        IBlockState state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

}
