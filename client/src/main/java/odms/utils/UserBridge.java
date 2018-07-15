package odms.utils;

import okhttp3.OkHttpClient;

public class UserBridge extends Bifrost {
    public UserBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public UserBridge(OkHttpClient client) {
        super(client);
    }
}
