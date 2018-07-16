package odms.utils;

import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ClinicianBridge extends Bifrost {
    public ClinicianBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public ClinicianBridge(OkHttpClient client) {
        super(client);
    }


    public Clinician getClinician(String wantedClinician, String token) throws ApiException {
        Response response = null;
        try {
            Headers headers = new Headers.Builder().add(TOKEN_HEADER, token).build();
            Request request = new Request.Builder()
                    .url(ip + "/clinicians/" + wantedClinician).headers(headers).build();
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.severe("failed to make request", e);
            return null;
        }
        if (response == null) {
            Log.warning("A null response was returned to the user");
            return null;
        }
        int responseCode = response.code();
        if (responseCode == 404 || responseCode == 401) {
            throw new ApiException(responseCode, "could not get requested clinician");
        } else if (responseCode == 500 || responseCode == 400) {
            Log.warning("An Error occurred. code returned: " + responseCode);
            throw new ApiException(responseCode, "error occurred when requesting clinician");
        } else if (responseCode != 200) {
            Log.warning("A non API response was returned code:" + responseCode);
            throw new ApiException(responseCode, "received unexpected response code");
        }

        try {
            return new JsonHandler().decodeClinician(response);
        } catch (IOException ex) {
            Log.severe("could not interpret the given clinician", ex);
            return null;
        }
    }
}
