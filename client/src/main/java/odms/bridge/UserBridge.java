package odms.bridge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import odms.commons.model.Disease;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.Medication;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.Log;
import odms.commons.utils.PhotoHelper;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class UserBridge extends RoleBridge {

    private static final String USERS = "/users/";
    public static final String FAILED_TO_PUT_TO = "Failed to PUT to ";
    public static final String COULD_NOT_MAKE_A_CALL_TO = "Could not make a call to ";
    public static final String FAILED_TO_POST_TO = "Failed to POST to ";

    public UserBridge(OkHttpClient client) {
        super(client);
    }

    public void getUsers(int startIndex, int count, String name, String region, String gender, String token) {
        String url = ip + "/users?startIndex=" + startIndex + "&count=" + count + "&name=" + name + "&region=" + region + "&gender=" + gender;
        Request request = new Request.Builder().header(tokenHeader, token).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AlertWindowFactory.generateError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Collection<UserOverview> overviews = new Gson().fromJson(response.body().string(), new TypeToken<Collection<UserOverview>>() {
                }.getType());
                for (UserOverview overview : overviews) {
                    AppController.getInstance().addUserOverview(overview);
                }
                response.close();
            }
        });
    }

    public void postUser(User user) {
        String url = ip + USERS;
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(user));
        Request request = new Request.Builder().post(requestBody).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make the call to POST /users");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to make POST call to /users");
                }
                response.close();
            }
        });
    }

    public User getUser(String nhi) throws IOException {
        User toReturn;
        String url = ip + USERS + nhi;
        Request request = new Request.Builder().url(url).build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    toReturn = handler.decodeUser(response);
                    toReturn.setProfilePhotoFilePath(getProfilePicture(nhi));
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

    public void putUser(User user, String nhi) {
        String url = ip + USERS + nhi;
        RequestBody body = RequestBody.create(json, new Gson().toJson(user));
        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url + " Response code: " + response.code());
                }
                response.close();
            }
        });
    }

    public void deleteUser(User user) {
        String url = ip + USERS + user.getNhi();
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
                response.close();
            }
        });
    }

    public void postUserProcedures(MedicalProcedure procedure, String nhi, String token) {
        String url = ip + USERS + nhi + "/procedures";
        RequestBody body = RequestBody.create(json, new Gson().toJson(procedure));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not POST procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to POST procedures to " + url);
                }
                response.close();
            }
        });
    }

    public void putUserProcedures(List<MedicalProcedure> procedures, String nhi, String token) {
        String url = ip + USERS + nhi + "/procedures";
        RequestBody body = RequestBody.create(json, new Gson().toJson(procedures));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url);
                }
                response.close();
            }
        });
    }

    public void postMedications(Medication medication, String nhi, String token) {
        String url = ip + USERS + nhi + "/medications";
        RequestBody body = RequestBody.create(json, new Gson().toJson(medication));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not POST procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_POST_TO + url);
                }
                response.close();
            }
        });
    }

    public void putMedications(List<Medication> medications, String nhi, String token) {
        String url = ip + USERS + nhi + "/medications";
        RequestBody body = RequestBody.create(json, new Gson().toJson(medications));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT medications to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url);
                }
                response.close();
            }
        });
    }

    public void postDiseases(Disease disease, String nhi, String token) {
        String url = ip + USERS + nhi + "/diseases";
        RequestBody body = RequestBody.create(json, new Gson().toJson(disease));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not POST diseases to " + url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_POST_TO + url);
                }
                response.close();
            }
        });
    }

    public void putDiseases(List<Disease> diseases, String nhi, String token) {
        String url = ip + USERS + nhi + "/diseases";
        RequestBody body = RequestBody.create(json, new Gson().toJson(diseases));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT to " + url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url);
                }
                response.close();
            }
        });
    }

    public void postReceivingOrgans(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving, String nhi, String token) {
        String url = ip + USERS + nhi + "/receiving";
        RequestBody body = RequestBody.create(json, new Gson().toJson(receiving));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning(COULD_NOT_MAKE_A_CALL_TO + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_POST_TO + url);
                }
                response.close();
            }
        });
    }

    public void putReceivingOrgans(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving, String nhi, String token) {
        String url = ip + USERS + nhi + "/receiving";
        RequestBody body = RequestBody.create(json, new Gson().toJson(receiving));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning(COULD_NOT_MAKE_A_CALL_TO + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url);
                }
                response.close();
            }
        });
    }

    public void postDonatingOrgans(Set<Organs> donating, String nhi) {
        String url = ip + USERS + nhi + "/donating";
        RequestBody body = RequestBody.create(json, new Gson().toJson(donating));
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning(COULD_NOT_MAKE_A_CALL_TO + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_POST_TO + url);
                }
                response.close();
            }
        });
    }

    public void putDonatingOrgans(Set<Organs> donating, String nhi) {
        String url = ip + USERS + nhi + "/donating";
        RequestBody body = RequestBody.create(json, new Gson().toJson(donating));
        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning(COULD_NOT_MAKE_A_CALL_TO + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException(FAILED_TO_PUT_TO + url);
                }
                response.close();
            }
        });
    }

    public String getProfilePicture(String nhi) throws IOException {
        String url = ip + USERS + nhi + "/photo";
        Request request = new Request.Builder().get().url(url).build();
        try(Response response  = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String contentType = response.header("Content-Type");
                String[] bits = contentType.split("/");
                String format = bits[bits.length-1];
                return handler.decodeProfilePicture(response.body(), nhi, format);
            } else if(response.code() == 404){
                return null;
            } else {
                throw new IOException("Failed to get profile picture");
            }
        }
    }

    public void putProfilePicture(String nhi, String profilePicturePath) throws IOException {
        String url = ip + USERS + nhi + "/photo";
        String[] bits = profilePicturePath.split("\\.");
        String format = bits[bits.length-1];
        byte[] bytesFromImage = PhotoHelper.getBytesFromImage(profilePicturePath);
        if(bytesFromImage.length == 0){
            return;
        }
        RequestBody body = RequestBody.create(MediaType.parse("image/"+format), bytesFromImage);
        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()) {
                    Log.warning("Failed to PUT " + url + " Response code: " + response.code());
                    throw new IOException("Could not PUT " + url);
                }
                response.close();
            }
        });
    }
    /**
     * checks whether a user can be found in the database
     * @param nhi nhi of the user to search for
     * @return true if the user can be found, false otherwise
     */
    public boolean getExists(String nhi) {
        Request request = new Request.Builder().get().url(ip + "/users/exists/" + nhi).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("could not determine if the admin exists", ex);
            return false;
        }
    }



}