package gigaherz.everpipe.common;

import net.minecraft.inventory.Container;

public abstract class ContainerBase extends Container
{
    public abstract void updateFields(int[] fields);
}
