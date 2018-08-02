package odms.bridge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.commons.utils.PhotoHelper;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class ClinicianBridge extends RoleBridge {
    public ClinicianBridge(OkHttpClient client) {
        super(client);
    }

    public void getClinicians(int startIndex, int count, String name, String region, String token) {
        String url = ip + "/clinicians?startIndex=" + startIndex + "&count=" + count + "&q=" + name + "&region=" + region;
        Request request = new Request.Builder().addHeader(TOKEN_HEADER, token).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                List<Clinician> clinicians = new Gson().fromJson(body.string(), new TypeToken<List<Clinician>>() {
                }.getType());
                for (Clinician clinician : clinicians) {
                    AppController.getInstance().addClinician(clinician);
                }
            }
        });
    }

    public void postClinician(Clinician clinician, String token) {
        String url = ip + "/clinicians";
        RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(requestBody).build();
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

    public void deleteClinician(Clinician clinician, String token) {
        String url = ip + "/clinicians/" + clinician.getStaffId();
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
            Clinician c = new JsonHandler().decodeClinician(response);
            c.setProfilePhotoFilePath(getProfilePicture(c.getStaffId(), token));
            return c;
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
        Request request = new Request.Builder().get().url(ip + "/clinicians/exists/" + staffId).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("could not determine if the clinician exists", ex);
            return false;
        }
    }

    private String getProfilePicture(String staffId, String token) throws IOException {
        String url = ip + "/clinicians/" + staffId + "/photo";
        Headers headers =  new Headers.Builder().add(TOKEN_HEADER, token).build();
        Request request = new Request.Builder().get().url(url).headers(headers).build();
        try(Response response  = client.newCall(request).execute()) {
            String contentType = response.header("Content-Type");
            String[] bits = contentType.split("/");
            String format = bits[bits.length-1];
            if (response.code() == 200) {
                return handler.decodeProfilePicture(response.body(), staffId,format);
            } else if(response.code() == 404){
                return null;
            } else {
                throw new IOException("Failed to get profile picture");
            }
        }
    }

    public void putProfilePicture(String staffId, String token, String profilePicturePath) throws IOException {
        String url = ip + "/clinicians/" + staffId + "/photo";
        String[] bits = profilePicturePath.split("\\.");
        String format = bits[bits.length-1];
        Headers headers = new Headers.Builder().add(TOKEN_HEADER, token).build();
        RequestBody body = RequestBody.create(MediaType.parse("image/" + format), PhotoHelper.getBytesFromImage(profilePicturePath));
        Request request = new Request.Builder().url(url).put(body).headers(headers).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    Log.warning("Failed to PUT " + url);
                    throw new IOException("Could not PUT " + url);
                }
            }
        });
    }

}
