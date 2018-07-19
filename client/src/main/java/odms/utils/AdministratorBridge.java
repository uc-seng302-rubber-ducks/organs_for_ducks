package odms.utils;

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
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(body).build();
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

    public void putAdmin(Administrator administrator, String username, String token) {
        String url = ip + "/admins/" + username;
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(administrator));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).put(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }

            }
        });

    }

    public void deleteAdmin(Administrator administrator, String token) {
        String url = ip + "/admins/" + administrator.getUserName();
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).delete().build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not DELETE " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to DELETE to " + url);
                }
            }
        });

    }






}
