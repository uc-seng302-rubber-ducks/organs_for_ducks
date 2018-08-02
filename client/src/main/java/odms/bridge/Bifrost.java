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
    protected String ip;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    protected static String TOKEN_HEADER;
    protected JsonHandler handler = new JsonHandler();

    public Bifrost(OkHttpClient client) {
        Properties prop = new ConfigPropertiesLoader().loadConfig("clientConfig.properties");
        TOKEN_HEADER = prop.getProperty("server.token.header", "x-auth-token");
        this.ip = prop.getProperty("server.url", "http://localhost:4941/odms/v1");
        this.client = client;
    }

}
