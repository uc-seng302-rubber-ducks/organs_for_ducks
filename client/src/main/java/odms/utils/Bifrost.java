package odms.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.Clinician;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

/**
 * Serves as a link between the client and the server
 */
public class Bifrost {

    protected OkHttpClient client;
    protected String ip;
    protected MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Bifrost(OkHttpClient client, String ip) {
        this.client = client;
        this.ip = ip;
    }

    public Bifrost(OkHttpClient client) {
        this(client, "http://localhost:4941/odms/v1");
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
}
