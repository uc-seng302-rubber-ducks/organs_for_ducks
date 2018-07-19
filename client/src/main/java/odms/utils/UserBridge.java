package odms.utils;

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
import okhttp3.*;

import java.io.IOException;
import java.util.*;

public class UserBridge extends Bifrost {

    public UserBridge(OkHttpClient client, String ip) {
        super(client, ip);
    }

    public UserBridge(OkHttpClient client) {
        super(client);
    }

    public Collection<UserOverview> loadUsersToController(int startIndex, int count, String name, String region, String gender, String token) throws IOException {
        String url = ip + "/users?startIndex=" + startIndex + "&count=" + count + "&name=" + name + "&region=" + region + "&gender=" + gender;
        Request request = new Request.Builder().header("x-auth-token", token).url(url).build();
        Collection<UserOverview> overviews;
        try (Response response = client.newCall(request).execute()) {
            overviews = new Gson().fromJson(response.body().string(), new TypeToken<Collection<UserOverview>>() {
            }.getType());
        } catch (IOException ex) {
            Log.warning("Error occurred while executing call to " + url, ex);
            throw ex;
        }
        return overviews;
    }

    public void postUser(User user) {
        String url = ip + "/users";
        System.out.println(new Gson().toJson(user));
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
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to make POST call to /users");
                }
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
                    toReturn = handler.decodeUser(response);
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
        String url = ip + "/users/" + nhi;
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(user));
        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("ahahahhahahhahaha");
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }

    public void deleteUser(User user) {
        String url = ip + "/users/" + user.getNhi();
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

    public void postUserProcedures(List<MedicalProcedure> procedures, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/procedures";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(procedures));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(body).build();
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
            }
        });
    }

    public void putUserProcedures(List<MedicalProcedure> procedures, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/procedures";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(procedures));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }

    public void postMedications(List<Medication> medications, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/medications";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(medications));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not POST procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to POST to " + url);
                }
            }
        });
    }

    public void putMedications(List<Medication> medications, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/medications";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(medications));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT procedures to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }

    public void postDiseases(List<Disease> diseases, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/diseases";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(diseases));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not POST diseases to " + url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to POST to " + url);
                }
            }
        });
    }

    public void putDiseases(List<Disease> diseases, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/diseases";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(diseases));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not PUT to " + url);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }

    public void postReceivingOrgans(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/receiving";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(receiving));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make a call to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to POST to " + url);
                }
            }
        });
    }

    public void putReceivingOrgans(Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving, String nhi, String token) {
        String url = ip + "/users/" + nhi + "/receiving";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(receiving));
        Request request = new Request.Builder().url(url).addHeader(TOKEN_HEADER, token).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make a call to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }

    public void postDonatingOrgans(Set<Organs> donating, String nhi) {
        String url = ip + "/users/" + nhi + "/donating";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(donating));
        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make a call to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to POST to " + url);
                }
            }
        });
    }

    public void putDonatingOrgans(List<Organs> donating, String nhi) {
        String url = ip + "/users/" + nhi + "/donating";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(donating));
        Request request = new Request.Builder().url(url).put(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Could not make a call to " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Failed to PUT to " + url);
                }
            }
        });
    }



}
