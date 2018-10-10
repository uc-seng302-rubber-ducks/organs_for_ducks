package odms.bridge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import odms.commons.exception.ApiException;
import odms.commons.model.Administrator;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.widget.CountableLoadingWidget;
import okhttp3.*;

import java.io.IOException;
import java.util.Collection;

/**
 * This class serves as a service layer between the Client and the server to make requests for administrator details
 * CRUD methods for interacting with the server
 */
public class AdministratorBridge extends RoleBridge {

    public static final String ADMINS = "/admins/";

    public AdministratorBridge(OkHttpClient client) {
        super(client);
    }

    /**
     * Makes a get request to the server fulfilling all the parameters provided
     *
     * @param startIndex     number of admins to skip before returning
     * @param count          number of admins to return
     * @param name           fetches admins whose names start with this
     * @param token          authentication token to put in the header
     * @param adminTableView loading table widget to turn off the waiting property of when the call has received a response
     */
    public void getAdmins(int startIndex, int count, String name, String token, CountableLoadingWidget adminTableView) {
        String url = ip + "/admins?startIndex=" + startIndex + "&count=" + count + "&q=" + name;
        Request request = new Request.Builder().url(url).addHeader("x-auth-token", token).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Collection<Administrator> administrators = new Gson().fromJson(response.body().string(), new TypeToken<Collection<Administrator>>() {
                }.getType());
                for (Administrator administrator : administrators) {
                    AppController.getInstance().addAdmin(administrator);
                }
                if (adminTableView != null) {
                    Platform.runLater(() -> adminTableView.setWaiting(false));
                }
                response.close();
            }
        });
    }


    /**
     * Makes a POST request to the server to create the admin
     * @param admin administrator to create
     * @param token authentication token (Admin level or higher)
     */
    public void postAdmin(Administrator admin, String token) {
        String url = ip + "/admins";
        RequestBody body = RequestBody.create(json, new Gson().toJson(admin));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Failed to call " + url, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.warning("Failed to POST to " + url);
                    throw new IOException("Failed to POST to " + url);
                }
                response.close();
            }
        });
    }

    /**
     * Makes a PUT request to the server to update the admin
     * @param administrator new administrator object to replace the old one with
     * @param username old username to replace
     * @param token authentication token (Admin level or higher)
     */
    public void putAdmin(Administrator administrator, String username, String token) {
        String url = ip + ADMINS + username;
        RequestBody requestBody = RequestBody.create(json, new Gson().toJson(administrator));
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).put(requestBody).build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("PUT", url));

    }

    /**
     * Makes a DELETE Request to the server to delete the admin
     * @param administrator administrator to delete
     * @param token authentication token
     */
    public void deleteAdmin(Administrator administrator, String token) {
        String url = ip + ADMINS + administrator.getUserName();
        Request request = new Request.Builder().url(url).addHeader(tokenHeader, token).delete().build();
        client.newCall(request).enqueue(CommonMethods.loggedCallback("DELETE", url));

    }

    /**
     * Method to get a single administrator
     * @param username username of adminstrator to get
     * @param token authentication token
     * @return administrator that is stored in the database
     * @throws ApiException if the response code is invalid
     */
    public Administrator getAdmin(String username, String token) throws ApiException {
        Response response = CommonMethods.getRole(client, ip, ADMINS, username, tokenHeader, token);
        if (response == null) {
            Log.warning("A null response was returned to the Admin");
            return null;
        }
        int responseCode = response.code();
        if (400 < responseCode && responseCode < 500) {
            //generic error occurred
            throw new ApiException(responseCode, "could not get requested Admin");
        } else if (responseCode >= 500) {
            //error but not my fault
            Log.warning("An Error occurred. code returned: " + responseCode);
            throw new ApiException(responseCode, "error occurred when requesting Admin");
        } else if (responseCode == 400) {
            throw new ApiException(responseCode, "bad request");
        } else if (responseCode != 200) {
            Log.warning("An unexpected response was returned with code:" + responseCode);
            throw new ApiException(responseCode, "received unexpected response Admin");
        }

        try {
            return new JsonHandler().decodeAdmin(response);
        } catch (IOException ex) {
            Log.severe("could not interpret the given Admin", ex);
            return null;
        } finally {
            response.close();
        }
    }

    /**
     * checks whether an admin exists in the database
     * @param username username to query for
     * @return true if admin is in the database, false otherwise
     */
    public boolean getExists(String username) {
        Request request = new Request.Builder().get().url(ip + "/admins/exists/" + username).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("could not determine if the admin exists", ex);
            return false;
        }
    }
}








