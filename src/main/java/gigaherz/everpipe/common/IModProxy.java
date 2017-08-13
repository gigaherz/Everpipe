package gigaherz.everpipe.common;

import gigaherz.everpipe.network.UpdateField;

public interface IModProxy
{
    default void handleUpdateField(UpdateField message) {}
}
