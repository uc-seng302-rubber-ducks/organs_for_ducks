package odms.bridge;

import odms.commons.utils.Log;
import okhttp3.*;

import java.io.IOException;

public class CommonMethods {

    private CommonMethods() {

    }

    /**
     * generic callback that does not require any further action
     *
     * @param url url being used (will only be used for logging failures)
     * @param method HTTP method used to make the call
     * @return standard handler that logs on failure and throws an exception on bad response code
     */
    public static Callback loggedCallback(String method, String url) {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not " + method + " to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to " + method + " to " + url);
                }
                response.close();
            }
        };
    }

    /**
     * abstracted method to get a role. expected use is for admins or clinicians.
     * sends to ip/urlRole/identifier with tokenHeader: token in the headers
     *
     * @param client      client to make the request with
     * @param ip          base ip address
     * @param urlRole     role qualifier
     * @param identifier  id/username to fetch
     * @param tokenHeader e.g. x-auth-token
     * @param token       token to authenticate with
     * @return raw Response from the server
     */
    public static Response getRole(OkHttpClient client, String ip, String urlRole, String identifier, String tokenHeader, String token) {
        try {
            Headers headers = new Headers.Builder().add(tokenHeader, token).build();
            Request request = new Request.Builder()
                    .url(ip + urlRole + identifier).headers(headers).build();
            return client.newCall(request).execute();
        } catch (IOException e) {
            Log.severe("failed to make request", e);
            return null;
        }
    }

}
