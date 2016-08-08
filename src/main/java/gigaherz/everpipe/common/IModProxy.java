package gigaherz.everpipe.common;

import gigaherz.everpipe.network.UpdateField;

public interface IModProxy
{
    void preInit();

    void init();

    void handleUpdateField(UpdateField message);
}
