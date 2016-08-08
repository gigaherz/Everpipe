package gigaherz.everpipe.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    //public static final int GUI_INTERFACE = 0;

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id)
        {
            /*case GUI_INTERFACE:
                if (tileEntity instanceof TileInterface)
                {
                    return new ContainerInterface((TileInterface) tileEntity, player.inventory);
                }
                break;*/
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        switch (id)
        {
            /*case GUI_INTERFACE:
                if (tileEntity instanceof TileInterface)
                {
                    return new GuiInterface(player.inventory, (TileInterface) tileEntity);
                }
                break;*/
        }

        return null;
    }
}
