package odms.bridge;

import okhttp3.OkHttpClient;

/**
 *
 */
public abstract class RoleBridge extends Bifrost {
    RoleBridge(OkHttpClient client) {
        super(client);
    }

    public abstract boolean getExists(String identifier);

}
