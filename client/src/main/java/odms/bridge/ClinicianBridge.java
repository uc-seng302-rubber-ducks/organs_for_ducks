package odms.bridge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.commons.utils.PhotoHelper;
import odms.controller.AppController;
import odms.controller.gui.widget.CountableLoadingWidget;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianBridge extends RoleBridge {

    private static final String CLINICIANS = "/clinicians/";

    public ClinicianBridge(OkHttpClient client) {
        super(client);
    }

    public void getClinicians(int startIndex, int count, String name, String region, String token, CountableLoadingWidget clinicianTableView) {
        String url = ip + "/clinicians?startIndex=" + startIndex + "&count=" + count + "&q=" + name + "&region=" + region;
        Request request = new Request.Builder().addHeader(tokenHeader, token).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Failed to get clinicians. On Failure Triggered", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body == null) {
                    Log.warning("The response body was null");
                    response.close();
                    return;
                }
                List<Clinician> clinicians = new Gson().fromJson(body.string(), new TypeToken<List<Clinician>>() {
                }.getType());
                for (Clinician clinician : clinicians) {
                    AppController.getInstance().addClinician(clinician);
                }
                if (clinicianTableView != null) {
                    Platform.runLater(() -> clinicianTableView.setWaiting(false));
                }
                response.close();
            }
        });
    }

    /**
     * Gets a list of clinicians from a particular region. Does not require authentication and returns their full name
     * and staff Id only
     * @param region that the clinicians are registered to
     * @return list of ComboBoxClinicians
     * @throws IOException if the call cannot be made
     */
    public List<ComboBoxClinician> getBasicClinicians(String region) throws IOException{
        List<ComboBoxClinician> returnList = new ArrayList<>();
        String url = ip + "/basic-clinicians/" + region;
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            if (body == null) {
                Log.warning("The response body was null");
                response.close();
                return returnList;
            }
            if (body.contentLength() == 2) { //if it returns empty array
                response.close();
                return returnList;
            }
            List<ComboBoxClinician> clinicians = new Gson().fromJson(body.string(), new TypeToken<List<ComboBoxClinician>>() {
            }.getType());
            returnList.addAll(clinicians);
        }
        response.close();
        return returnList;
    }

    public void postClinician(Clinician clinician, String token) {
        String url = ip + "/clinicians";
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(requestBody).build();
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
                response.close();
            }
        });
    }

    public void putClinician(Clinician clinician, String staffID, String token) {
        String url = ip + CLINICIANS + staffID;
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(clinician));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(requestBody).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("PUT", url));
    }

    public void deleteClinician(Clinician clinician, String token) {
        String url = ip + CLINICIANS + clinician.getStaffId();
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).delete().build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("DELETE", url));
    }

    public Clinician getClinician(String wantedClinician, String token) throws ApiException {
        Response response = CommonMethods.getRole(client, ip, CLINICIANS, wantedClinician, tokenHeader, token);
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
            if (ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("false")) {
                c.setProfilePhotoFilePath(getProfilePicture(c.getStaffId(), token));
                return c;
            }
            return c;
        } catch (IOException ex) {
            Log.severe("could not interpret the given clinician", ex);
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * checks whether a clinician exists in the database by staff id
     *
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

    public String getProfilePicture(String staffId, String token) throws IOException {
        String url = ip + CLINICIANS + staffId + "/photo";
        Headers headers = new Headers.Builder().add(tokenHeader, token).build();
        Request request = new Request.Builder().get().url(url).headers(headers).build();
        try (Response response = client.newCall(request).execute()) {
            String contentType = response.header("Content-Type");
            if (contentType == null) {
                Log.warning("The content-type in the header was null");
                return null;
            }
            String[] bits = contentType.split("/");
            String format = bits[bits.length - 1];
            if (response.code() == 200) {
                return handler.decodeProfilePicture(response.body(), staffId, format);
            } else if (response.code() == 404) {
                return null;
            } else {
                throw new IOException("Failed to get profile picture");
            }
        }
    }

    public void putProfilePicture(String staffId, String token, String profilePicturePath) throws IOException {
        String url = ip + CLINICIANS + staffId + "/photo";
        String[] bits = profilePicturePath.split("\\.");
        String format = bits[bits.length - 1];
        byte[] bytesFromImage = PhotoHelper.getBytesFromImage(profilePicturePath);
        if (bytesFromImage.length == 0) {
            return;
        }
        Headers headers = new Headers.Builder().add(tokenHeader, token).build();
        RequestBody body = RequestBody.create(MediaType.parse("image/" + format), bytesFromImage);
        Request request = new Request.Builder().url(url).put(body).headers(headers).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.warning("Failed to PUT " + url);
                    throw new IOException("Could not PUT " + url);
                }
                response.close();
            }
        });
    }

}
