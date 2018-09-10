package odms.bridge;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import okhttp3.*;

import java.io.IOException;

public class AppointmentsBridge extends Bifrost {
    private static final String APPOINTMENTS = "/appointments";
    private boolean quiet;

    public AppointmentsBridge(OkHttpClient client) {
        super(client);
        quiet = false;
    }

    /**
     * Constructor to create a new Appointments bridge
     *
     * @param client OkhttpClient to make the calls with.
     * @param quiet  Determines if alert windows are shown if there is an error.
     */
    AppointmentsBridge(OkHttpClient client, boolean quiet) {
        this(client);
        this.quiet = quiet;
    }


    /**
     * Checks if the given user has a pending appointment request
     *
     * @param nhi unique identifier of the user
     * @return true if the user has a pending appointment request, false otherwise
     */
    public boolean pendingExists(String nhi) {
        String url = String.format("%s/users/%s%s/exists?status=%d", ip, nhi, APPOINTMENTS, AppointmentStatus.PENDING.getDbValue());
        Request request = new Request.Builder().get().url(url).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("", ex);
            return false;
        }
    }


    /**
     * Gets all the appointments
     *
     * @param count    the number of results to return
     * @param toAddTo  Observable list to add to.
     * @param user     user's unique ID
     * @param userType UserType to determine whether they are a user, clinician or admin.
     */
    public void getAppointments(int count, int start, ObservableList<Appointment> toAddTo, String user, UserType userType) {
        String url = null;
        if (userType.equals(UserType.USER)) {
            url = String.format("%s/users/%s%s?count=%d&startIndex=%d", ip, user, APPOINTMENTS, count, start);
        } else if (userType.equals(UserType.CLINICIAN)) {
            url = String.format("%s/clinicians/%s%s?count=%d&startIndex=%d", ip, user, APPOINTMENTS, count, start);
        }

        if (url == null) {
            return;
        }
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        String bodyString = response.body().string();
                        toAddTo.addAll(new JsonHandler().decodeAppointments(bodyString));
                    }
                } else {
                    logAndNotify(response);
                }
                response.close();
            }
        });
    }

    /**
     * Fire a post request to the server for creating appointments
     *
     * @param appointment Appointment to create
     */
    public void postAppointment(Appointment appointment) {
        String url = String.format("%s%s", ip, APPOINTMENTS);
        RequestBody body = RequestBody.create(json, new Gson().toJson(appointment));
        Request request = new Request.Builder().post(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
                response.close();
            }
        });
    }


    /**
     * Fires a delete request to the server for the given appointment
     *
     * @param appointment Appointment to be deleted
     */
    public void deleteAppointment(Appointment appointment) {
        String url = String.format("%s%s", ip, APPOINTMENTS);
        RequestBody body = RequestBody.create(json, new Gson().toJson(appointment));
        Request request = new Request.Builder().delete(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
            }
        });
    }


    /**
     * Fires a put request to the server to update the appointment
     *
     * @param appointment the updated appointment
     */
    public void putAppointment(Appointment appointment) {
        String url = String.format("%s/clinicians/%s%s/%d", ip, appointment.getRequestedClinicianId(), APPOINTMENTS, appointment.getAppointmentId());
        RequestBody body = RequestBody.create(json, new Gson().toJson(appointment));
        Request request = new Request.Builder().put(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
            }
        });
    }

    /**
     * Logs a bad response
     *
     * @param response response to log
     */
    private void logResponse(Response response) {
        Log.warning("Bad response code from server. Error code: " + response.code() + ". Response was: \n" + response.message());
    }

    /**
     * Alerts user with a alert window containing the given message
     *
     * @param message message to display to the user.
     */
    private void alertUser(String message) {
        if (!quiet) {
            Platform.runLater(() -> AlertWindowFactory.generateError(message));
        }
    }

    /**
     * Checks the response code and logs the response. Depending on the response code, it will alert the user whether
     * its their fault or not.
     *
     * @param response response check
     */
    private void logAndNotify(Response response) {
        if (response.code() >= 400 && response.code() < 404) {
            logResponse(response);
            alertUser("Oops! Something went wrong. Please check your inputs and try again.");
        } else {
            logResponse(response);
            alertUser("Oops! Something went wrong. Please try again later.");
        }
    }
}
