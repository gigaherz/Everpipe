package gigaherz.everpipe.pipe.connectors;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class ConnectorStateData
{
    public ImmutableMultimap<EnumFacing, ConnectorState> connectors;

    public ImmutableMultimap<EnumFacing, ConnectorState> getConnectors()
    {
        return connectors;
    }

    public ConnectorStateData(Multimap<EnumFacing, Connector> connectors)
    {
        ImmutableMultimap.Builder<EnumFacing, ConnectorState> builder = ImmutableMultimap.<EnumFacing, ConnectorState>builder();
        for(Map.Entry<EnumFacing,Connector> entry : connectors.entries())
        {
            builder.put(entry.getKey(), entry.getValue().getImmutableState());
        }
        this.connectors = builder.build();
    }
}
