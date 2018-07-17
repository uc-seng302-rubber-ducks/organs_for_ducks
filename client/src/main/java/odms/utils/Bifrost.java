package odms.utils;

import okhttp3.*;


/**
 * Serves as a link between the client and the server
 */
public class Bifrost {

    protected OkHttpClient client;
    protected String ip;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Bifrost(OkHttpClient client, String ip) {
        this.client = client;
        this.ip = ip;
    }

    public Bifrost(OkHttpClient client) {
        this(client, "http://localhost:4941/odms/v1");
    }

}
