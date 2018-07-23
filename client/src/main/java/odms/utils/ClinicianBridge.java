package odms.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.utils.JsonHandler;
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

    public void postClinician(Clinician clinician, String token) {
        String url = ip + "/clinicians";
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).post(requestBody).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make the call to POST /clinicians", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.warning("Failed to POST to " + url);
                    throw new IOException("Failed to make POST call to " + url);
                }

            }
        });
    }

    public void putClinician(Clinician clinician, String staffID, String token) {
        String url = ip + "/clinicians/" + staffID;
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).put(requestBody).build();
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

    public void deleteClinician(Clinician clinician, String token) {
        String url = ip + "/clinicians/" + clinician.getStaffId();
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).delete().build();
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

    public Clinician getClinician(String wantedClinician, String token) throws ApiException {
        Response response;
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

    /**
     * checks whether a clinician exists in the db by staff id
     * @param staffId staff id to search for
     * @return true if staff id can be found, false otherwise
     */
    public boolean getExists(String staffId) {
        Request request = new Request.Builder().get().url(ip + "/clinicians/exists" + staffId).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("could not determine if the clinician exists", ex);
            return false;
        }
    }
}
