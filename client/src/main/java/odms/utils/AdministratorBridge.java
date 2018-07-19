package odms.utils;

import odms.commons.model.Administrator;
import odms.commons.utils.JsonHandler;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.Administrator;
import odms.commons.utils.Log;
import okhttp3.*;

import java.io.IOException;
import java.util.Collection;

public class AdministratorBridge extends Bifrost {

    public AdministratorBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public AdministratorBridge(OkHttpClient client) {
        super(client);
    }

    public Collection<Administrator> getAdmins(int startIndex, int count, String name, String region, String token) throws IOException {
        String url = ip + "/admins?startIndex=" + startIndex + "&count=" + count + "&q=" + name;
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).build();
        Response response = client.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), new TypeToken<Collection<Administrator>>() {
        }.getType());
    }

    /**
     * makes a request and decodes it to return an Administrator
     *
     * @param username user to be returned
     * @param token login token of an authenticated session
     * @return administrator to be returned
     * @throws IOException Thrown on an invalid response being passed to the handler
     */
    public Administrator getAdmin(String username, String token) throws IOException {
        String url = ip + "/admins/" + username;
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).build();
        Response response = client.newCall(request).execute();
        return handler.decodeAdmin(response);
    }


    public void postAdmin(Administrator admin, String token) {
        String url = ip + "/admins";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(admin));
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Failed to call " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.warning("Failed to POST to " + url);
                    throw new IOException("Failed to POST to " + url);
                }
            }
        });
    }
}
