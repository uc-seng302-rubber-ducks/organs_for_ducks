package odms.bridge;

import com.google.gson.Gson;
import odms.commons.utils.Log;
import okhttp3.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CountriesBridge extends Bifrost {


    public CountriesBridge(OkHttpClient client) {
        super(client);
    }


    /**
     * Returns the Set of allowed countries by the administrator
     *
     * @return Countries allowed to be resided in; empty set on error
     * @throws IOException Thrown on result set failure
     */
    public Set getAllowedCountries() throws IOException {
        String url = ip + "/countries";
        Request request = new Request.Builder().get().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return handler.decodeCountries(response);
        }
        response.close();
        return new HashSet();
    }


    /**
     * Puts the set of allowed countries onto the database
     *
     * @param countries set of allowed countries
     * @param token     authentication token
     */
    public void putAllowedCountries(Set countries, String token) {
        String url = ip + "/countries";
        RequestBody body = RequestBody.create(json, new Gson().toJson(countries));
        Request request = new Request.Builder().addHeader(tokenHeader, token).put(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not put to the countries endpoint");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to put to the countries endpoint");
                }
                response.close();
            }
        });

    }
}
