package odms.utils;

import okhttp3.OkHttpClient;

/**
 *
 */
public abstract class RoleBridge extends Bifrost {
    public RoleBridge(OkHttpClient client) {
        super(client);
    }

    public abstract boolean getExists(String identifier);

}
