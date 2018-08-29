package odms.commons.model;

import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.Log;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Appointment class that holds information about a user's appointment request to a clinician
 * Has two booleans describing the state of the appointment
 */
public class Appointment {

    private String appointmentId;

    private User requestingUser;

    private Clinician requestedClinician;

    private AppointmentCategory appointmentCategory;

    private LocalDateTime requestedDate;

    private String requestDescription;

    private AppointmentStatus appointmentStatus;

    private boolean seen;

    /**
     * Empty constructor for Appointment. Useful for creating one from the database or specific customisation for tests.
     * Be careful that an Appointment created by this method may have null fields
     */
    public Appointment() {
        //General purpose empty constructor
    }

    /**
     * Constructor for Appointment class. Must take every information type (except the boolean hasSeen) that is associated with an Appointment.
     * @param requestingUser User requesting the appointment
     * @param requestedClinician clinician being requested to have the appointment with
     * @param appointmentCategory the generic type of the appointment
     * @param requestedDate date the appointment is requested to be on
     * @param requestDescription a more detailed description of the appointment, possibly including a reason why it was requested
     * @param appointmentStatus the status of the appointment. This can be pending, accepted, rejected, or cancelled.
     */
    public Appointment(User requestingUser, Clinician requestedClinician, AppointmentCategory appointmentCategory, LocalDateTime requestedDate, String requestDescription, AppointmentStatus appointmentStatus) {
        this.requestingUser = requestingUser;
        this.requestedClinician = requestedClinician;
        this.appointmentCategory = appointmentCategory;
        this.requestedDate = requestedDate;
        this.requestDescription = requestDescription;
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public User getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(User requestingUser) {
        this.requestingUser = requestingUser;
    }

    public Clinician getRequestedClinician() {
        return requestedClinician;
    }

    public void setRequestedClinician(Clinician requestedClinician) {
        this.requestedClinician = requestedClinician;
    }

    public AppointmentCategory getAppointmentCategory() {
        return appointmentCategory;
    }

    public void setAppointmentCategory(AppointmentCategory appointmentCategory) {
        this.appointmentCategory = appointmentCategory;
    }

    public LocalDateTime getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDateTime requestedDate) {
        this.requestedDate = requestedDate;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean hasSeen) {
        this.seen = hasSeen;
    }

    @Override
    public int hashCode() {

        return Objects.hash(requestingUser, requestedClinician, appointmentCategory, requestedDate, requestDescription, appointmentStatus, seen);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment appointment = (Appointment) o;
        if (appointmentId == null || appointment.appointmentId == null) {
            Log.warning("Trying to compare appointments when at least one does not have a unique id. Comparison failed.");
            return false;
        }
        return appointmentId.equals(appointment.appointmentId);
    }
}
