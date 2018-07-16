package odms.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Serves as a link between the client and the server
 */
public class Bifrost {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String TOKEN_HEADER = "x-auth-token";
    private OkHttpClient client;
    private String ip;
    private JsonHandler handler = new JsonHandler();

    public Bifrost(OkHttpClient client, String ip) {
        this.client = client;
        this.ip = ip;
    }

    public Bifrost(OkHttpClient client) {
        this(client, "http://localhost:4941/odms/v1");
    }

    public JsonHandler getHandler() {
        return handler;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public String getIp() {
        return ip;
    }

    public MediaType getJSON() {
        return JSON;
    }

    public void getUsers(AppController controller, int startIndex, int count, String name, String region) {
        String url = ip + "/users?startIndex=" + startIndex + "&count=" + count;
        Request request = new Request.Builder().url(url).build();
        User toReturn = null;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not execute call to /users/{nhi}");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        Set<UserOverview> users = new Gson().fromJson(body.string(), new TypeToken<HashSet<UserOverview>>() {
                        }.getType());
                        controller.setUserOverviews(users);
                    }
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
    }

    public void getClinicians(AppController controller, int startIndex, int count, String token) {
        String url = ip + "/clinicians?startIndex=" + startIndex + "&count=" + count;
        Request request = new Request.Builder().addHeader("x-auth-token", token).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make the call to /clinicians");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        List<Clinician> clinicians = new Gson().fromJson(body.string(), new TypeToken<List<Clinician>>() {
                        }.getType());
                        controller.setClinicians(clinicians);
                    }
                }
            }
        });
    }

    public void postUser(User user) {
        String url = ip + "/users";
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(user));
        Request request = new Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make the call to POST /users");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //Don't need to do anything with the response
            }
        });
    }

    public User getUser(String nhi) throws IOException {
        User toReturn;
        String url = ip + "/users/" + nhi;
        Request request = new Request.Builder().url(url).build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    toReturn = new Gson().fromJson(response.body().string(), User.class);
                } else {
                    toReturn = null;
                }
            }
        } catch (IOException ex) {
            Log.warning("Could not make GET call to /users/" + nhi, ex);
            throw ex;
        }
        return toReturn;
    }
}
