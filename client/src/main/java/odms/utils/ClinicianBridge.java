package odms.utils;

import okhttp3.OkHttpClient;

public class ClinicianBridge extends Bifrost {
    public ClinicianBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public ClinicianBridge(OkHttpClient client) {
        super(client);
    }


}
