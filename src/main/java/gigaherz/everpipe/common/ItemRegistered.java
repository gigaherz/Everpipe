package gigaherz.everpipe.common;

import gigaherz.everpipe.Everpipe;
import net.minecraft.item.Item;

public class ItemRegistered extends Item
{
    public ItemRegistered(String name)
    {
        setRegistryName(name);
        setUnlocalizedName(Everpipe.MODID + "." + name);
    }
}
