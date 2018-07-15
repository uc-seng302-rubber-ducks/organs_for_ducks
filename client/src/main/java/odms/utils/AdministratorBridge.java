package odms.utils;

import okhttp3.OkHttpClient;

public class AdministratorBridge extends Bifrost {

    public AdministratorBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public AdministratorBridge(OkHttpClient client) {
        super(client);
    }


}
