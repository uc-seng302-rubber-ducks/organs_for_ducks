package odms.utils;

import odms.commons.utils.JsonHandler;
import okhttp3.*;


/**
 * Serves as a link between the client and the server
 */
public class Bifrost {

    protected OkHttpClient client;
    protected String ip;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static final String TOKEN_HEADER = "x-auth-token";
    protected JsonHandler handler = new JsonHandler();

    public Bifrost(OkHttpClient client, String ip) {
        this.client = client;
        this.ip = ip;
    }

    public Bifrost(OkHttpClient client) {
        this(client, "http://localhost:4941/odms/v1");
    }

}
