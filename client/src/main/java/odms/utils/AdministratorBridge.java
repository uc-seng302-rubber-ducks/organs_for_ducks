package odms.utils;

import odms.commons.model.Administrator;
import odms.commons.utils.JsonHandler;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;

public class AdministratorBridge extends Bifrost {

    public AdministratorBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public AdministratorBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * gets the administrator object to use for the session
     *
     * @param wantedAdmin administrator to log in as
     * @param token authentication token
     * @return administrator for use in the session
     * @throws IOException Thrown when the returned json is bad
     */
    private Administrator getAdmin(String wantedAdmin, String token) throws IOException {

        String url = super.ip + "admins/"+ wantedAdmin;
        Response response = getResponse(token, url);
        if (response == null) return null;

        return new JsonHandler().decodeAdmin(response);
    }


}
