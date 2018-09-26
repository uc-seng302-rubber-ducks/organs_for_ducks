package odms.bridge;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SQLBridge extends Bifrost {

    public SQLBridge(OkHttpClient client) {
        super(client);
    }

    public List<String> executeSqlStatement(String query, String token) throws IOException {
        String url = ip + "/sql";
        RequestBody body = RequestBody.create(json, new Gson().toJson(query));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return handler.decodeQueryResult(response.body());
        }
        return new ArrayList<>();
    }
}
