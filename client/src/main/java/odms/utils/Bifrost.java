package odms.utils;

import com.google.gson.Gson;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * Serves as a link between the client and the server
 */
public class Bifrost {

    private OkHttpClient client;
    private static Bifrost instance;
    private String ip;

    private Bifrost(OkHttpClient client, String ip) {
        this.client = client;
        this.ip = ip;
    }

    public static Bifrost getInstance() {
        if (instance == null) {
            instance = new Bifrost(new OkHttpClient(), "http://localhost:4941");
        }
        return instance;
    }

    public List<User> getUsers(AppController controller) {
        String url = ip + "/api/v1/users?startIndex=0&count=4";
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
                        List<UserOverview> users = new Gson().fromJson(body.string(), List.class);
                        System.out.println(users);
                        call.cancel();
                        for (UserOverview user : users) {
                            System.out.println(user);
                        }
                    }
                } else {
                    throw new IOException("Unexpected code " + response);
                }
            }
        });
        return controller.getUsers();
    }

    public static void main(String[] argv) {
        Bifrost bifrost = Bifrost.getInstance();
        System.out.println(bifrost.getUsers(AppController.getInstance()));
    }
}
