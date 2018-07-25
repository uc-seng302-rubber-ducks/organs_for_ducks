package odms.utils;

import com.google.gson.JsonObject;
import odms.commons.exception.ApiException;
import odms.commons.utils.HttpRequester;
import odms.commons.utils.Log;
import okhttp3.*;

import java.io.IOException;

public class LoginBridge extends Bifrost{
    public LoginBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * requests to log in as the specified role with details, returning the token if successful
     * @param wanted username/id to attempt to use
     * @param password plaintext attempted password
     * @param role desired role (admin or clinician)
     *             see UserRole in the server project
     * @return token if request is successful
     * @throws ApiException if any response other than the expected token is returned
     */
    public String loginToServer(String wanted, String password, String role) throws ApiException{
        Response response;
        JsonObject body = new JsonObject();
        body.addProperty("username" , wanted);
        body.addProperty("password", password);
        body.addProperty("role", role);

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body.toString());
        HttpRequester requester = new HttpRequester(client);
        try {
            Request request = new Request.Builder().url(ip + "/login").post(requestBody).build();
            response = requester.makeRequest(request);
        } catch (IOException e) {
            Log.severe("A network error occurred.", e);
            return null;
        }
        if (response == null) {
            Log.warning("A null response was returned to the user");
            throw new ApiException(0, "null value return when making the login request");
        }
        int responseCode = response.code();
        if(responseCode == 404 || responseCode == 401) {
            throw new ApiException(responseCode, "could not log in as the requested user");
        } else if (responseCode == 500 || responseCode == 400) {
            Log.warning("An Error occurred. code returned: " + responseCode);
            throw new ApiException(responseCode, "error code recieved");
        } else if (responseCode != 200) {
            Log.warning("A non API response was returned code:" + responseCode);
            throw new ApiException(responseCode, "unexpected response code received");
        }

        return handler.decodeLogin(response);
    }

}
