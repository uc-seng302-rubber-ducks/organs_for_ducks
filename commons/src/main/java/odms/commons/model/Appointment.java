package odms.commons.model;

import java.time.LocalDateTime;

/**
 * Appointment class that holds information about a user's appointment request to a clinician
 * Has two booleans describing the state of the appointment
 */
public class Appointment {

    private String appointmentId;

    private User requestingUser;

    private Clinician requestedClinician;

    private String appointmentType;
    //private AppointmentType appointmentType; //TODO Replace the string with this when Enum is created. James 27/8

    private LocalDateTime requestedDate;

    private String requestDescription;

    private boolean isAccepted;

    private boolean isPending;

    public Appointment() {
        //General purpose empty constructor
    }

    public Appointment(String appointmentId, User requestingUser, Clinician requestedClinician, String appointmentType, LocalDateTime requestedDate, String requestDescription) {

    }

    /**
     * Generate a new unique id for an appointment
     * @return Unique string id.
     */
    public String generateId() {
        //unique generator function
        return "";
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

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
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

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }
}
