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

    private Integer appointmentId;

    private String requestingUserId;

    private String requestedClinicianId;

    private AppointmentCategory appointmentCategory;

    private LocalDateTime requestedDate;

    private String requestDescription;

    private AppointmentStatus appointmentStatus;

    private boolean seen;

    private String rejectionReason;

    private String title = null; //This is used over nhi if clinician has a personal appointment

    /**
     * Empty constructor for Appointment. Useful for creating one from the database or specific customisation for tests.
     * Be careful that an Appointment created by this method may have null fields
     */
    public Appointment() {
        //General purpose empty constructor
    }

    /**
     * Constructor for Appointment class. Must take every information type (except the boolean hasSeen) that is associated with an Appointment.
     * @param requestingUserId User requesting the appointment
     * @param requestedClinicianId clinician being requested to have the appointment with
     * @param appointmentCategory the generic type of the appointment
     * @param requestedDate date the appointment is requested to be on
     * @param requestDescription a more detailed description of the appointment, possibly including a reason why it was requested
     * @param appointmentStatus the status of the appointment. This can be pending, accepted, rejected, or cancelled.
     */
    public Appointment(String requestingUserId, String requestedClinicianId, AppointmentCategory appointmentCategory, LocalDateTime requestedDate, String requestDescription, AppointmentStatus appointmentStatus) {
        this.requestingUserId = requestingUserId;
        this.requestedClinicianId = requestedClinicianId;
        this.appointmentCategory = appointmentCategory;
        this.requestedDate = requestedDate;
        this.requestDescription = requestDescription;
        this.appointmentStatus = appointmentStatus;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getRequestingUserId() {
        return requestingUserId;
    }

    public void setRequestingUserId(String requestingUser) {
        this.requestingUserId = requestingUser;
    }

    public String getRequestedClinicianId() {
        return requestedClinicianId;
    }

    public void setRequestedClinicianId(String requestedClinicianId) {
        this.requestedClinicianId = requestedClinicianId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean hasSeen) {
        this.seen = hasSeen;
    }

    @Override
    public int hashCode() {

        return Objects.hash(requestingUserId, requestedClinicianId, appointmentCategory, requestedDate, requestDescription, appointmentStatus, seen);
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

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
