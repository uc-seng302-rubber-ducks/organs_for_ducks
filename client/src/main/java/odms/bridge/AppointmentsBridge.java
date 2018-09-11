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
     * Checks if the given user/clinician has appointments with the given status
     *
     * @param id     The unique identifier of the user/clinician
     * @param type   Role specifying either a user or a clinician
     * @param status The status of the appointment to be checked for
     * @return       True if the user/clinician has appointments with the given status, false otherwise
     */
    public boolean checkAppointmentStatusExists(String id, UserType type, AppointmentStatus status) {
        String url = "";
        if (type == UserType.USER) {
            url = String.format("%s/users/%s%s/exists?status=%d", ip, id, APPOINTMENTS, status.getDbValue());

        } else if (type == UserType.CLINICIAN) {
            url = String.format("%s/clinicians/%s%s/exists?status=%d", ip, id, APPOINTMENTS, status.getDbValue());
        }

        Request request = new Request.Builder().get().url(url).build();

        try (Response res = client.newCall(request).execute()) {
            return res.body().string().equalsIgnoreCase("true");
        } catch (NullPointerException | IOException ex) {
            Log.warning("Failed to check for cancelled appointments", ex);
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
     * Gets all the appointments for a specified clinician
     *
     * @param startIndex             Index to get results from
     * @param count                  Amount of results to get at once
     * @param staffId                Id of the staff to get appointments for
     * @param token                  Authentication required to get the appointments.
     * @param observableAppointments List to update with the gotten appointments
     */
    public void getClinicianAppointments(int startIndex, int count, String staffId, String token, ObservableList<Appointment> observableAppointments) {
        String url = String.format("%s/clinicians/%s%s?count=%d&startIndex=%d", ip, staffId, APPOINTMENTS, count, startIndex);
        Request request = new Request.Builder().addHeader(tokenHeader, token).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.warning("Failed to get clinicians. On Failure Triggered", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response == null) {
                    Log.warning("A null response was returned to the user");
                    return;
                }
                ResponseBody body = response.body();
                if (body == null) {
                    Log.warning("A null response body was returned to the user");
                    return;
                }
                String bodyString = response.body().string();

                Platform.runLater(() -> {
                    observableAppointments.clear();
                    observableAppointments.addAll(new JsonHandler().decodeAppointments(bodyString));
                });
            }
        });
    }

    /**
     * Calls the server and asks if the user has any appointments that are accepted or rejected but not seen
     * @param nhi of the user that is being checked for unseen appointments
     * @return An appointment that is unseen if it exists, otherwise null.
     */
    public Appointment getUnseenAppointment(String nhi) {
        String url = String.format("%s/users/%s%s/unseen", ip, nhi, APPOINTMENTS);
        Request request = new Request.Builder().get().url(url).build();

        try (Response res = client.newCall(request).execute()) {
            if (res.body() != null) {
                return new JsonHandler().decodeOneAppointment(res.body().string());
            } else {
                Log.warning("The response body was null");
                return null;
            }
        } catch (NullPointerException | IOException ex) {
            Log.warning("", ex);
            return null;
        }
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
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
            }
        });
    }

    /**
     * Fire a patch request to the server for updating the status of an appointment
     * @param appointmentId Id of the appointment to be updated.
     * @param statusId status to be changed to.
     */
    public void patchAppointmentStatus(Integer appointmentId, int statusId) {
        String url = String.format("%s%s", ip, APPOINTMENTS + "/" + appointmentId + "/status");
        RequestBody body = RequestBody.create(json, new Gson().toJson(statusId));
        Request request = new Request.Builder().patch(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
                response.close();
            }
        });
    }


    /**
     * Deletes all cancelled appointments belonging to the given user type that they have seen.
     *
     * @param id   unique identifier of the user / clinician
     * @param role specifies if the given user type is a user or clinician
     */
    public void deleteCancelledAppointments(String id, UserType role) {
        String url = "";
        if (role == UserType.USER) {
            url = String.format("%s/users/%s%s/cancelled", ip, id, APPOINTMENTS);
        } else if (role == UserType.CLINICIAN) {
            url = String.format("%s/clinicians/%s%s/cancelled", ip, id, APPOINTMENTS);
        }
        RequestBody body = RequestBody.create(json, new Gson().toJson(id));
        Request request = new Request.Builder().delete(body).url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.severe(e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (!response.isSuccessful()) {
                    logAndNotify(response);
                }
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
