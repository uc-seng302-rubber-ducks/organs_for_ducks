package odms.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.Clinician;
import odms.commons.utils.Log;
import odms.controller.AppController;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ClinicianBridge extends Bifrost {
    public ClinicianBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public ClinicianBridge(OkHttpClient client) {
        super(client);
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

    public void postClinician(Clinician clinician) {
        String url = ip + "/clinicians";
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(clinician));
        Request request = new Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make the call to POST /clinicians", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to make POST call to /clinicians");
                }

            }
        });
    }

    public Clinician getClinician(String staffID) throws IOException {
        Clinician toReturn;
        String url = ip + "/clinicians/" + staffID;
        Request request = new Request.Builder().url(url).build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    toReturn = new Gson().fromJson(response.body().string(), Clinician.class);
                } else {
                    toReturn = null;
                }
            }
        } catch (IOException ex) {
            Log.warning("Could not make GET call to /clinicians/" + staffID, ex);
            throw ex;
        }
        return toReturn;
    }

    public void patchClinician(Clinician clinician, String staffID) {
        String url = ip + "/clinicians/" + staffID;
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).patch(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PATCH to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PATCH to " + url);
                }
            }
        });
    }

    public void deleteClinician(Clinician clinician) {
        String url = ip + "/clinicians/" + clinician.getStaffId();
        Request request = new Request.Builder().url(url).delete().build();
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
