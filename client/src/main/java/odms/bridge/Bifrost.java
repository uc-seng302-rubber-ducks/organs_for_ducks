package odms.bridge;

import odms.commons.config.ConfigPropertiesSession;
import odms.commons.utils.JsonHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;


/**
 * Stores data for bridges
 */
public class Bifrost {

    protected OkHttpClient client;
    protected MediaType json = MediaType.parse("application/json; charset=utf-8");
    protected JsonHandler handler = new JsonHandler();
    String ip;
    String tokenHeader;

    Bifrost(OkHttpClient client) {
        tokenHeader = ConfigPropertiesSession.getInstance().getProperty("server.token.header", "x-auth-token");
        this.ip = ConfigPropertiesSession.getInstance().getProperty("server.url");
        this.client = client;
    }

}
