package odms.bridge;

import odms.commons.utils.ConfigPropertiesLoader;
import odms.commons.utils.JsonHandler;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.util.Properties;


/**
 * Serves as a link between the client and the server
 */
public class Bifrost {

    protected OkHttpClient client;
    protected MediaType json = MediaType.parse("application/json; charset=utf-8");
    String ip;
    String tokenHeader;
    protected JsonHandler handler = new JsonHandler();

    Bifrost(OkHttpClient client) {
        Properties prop = new ConfigPropertiesLoader().loadConfig("clientConfig.properties");
        tokenHeader = prop.getProperty("server.token.header", "x-auth-token");
        this.ip = prop.getProperty("server.url", "http://localhost:4941/odms/v1");
        this.client = client;
    }

}
