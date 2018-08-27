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

    private String appointmentCategory;
    //private AppointmentCategory appointmentCategory; //TODO Replace the string with this when Enum is created. James 27/8

    private LocalDateTime requestedDate;

    private String requestDescription;

    private String appointmentStatus;
    //private AppointmentStatus appointmentStatus; //TODO Replace the string with this when Enum is created. James 27/8

    private boolean hasSeen;

    /**
     * Empty constructor for Appointment. Useful for creating one from the database or specific customisation for tests.
     * Be careful that an Appointment created by this method may have null fields
     */
    public Appointment() {
        //General purpose empty constructor
    }

    /**
     * Constructor for Appointment class. Must take every information type (except the boolean hasSeen) that is associated with an Appointment.
     * @param appointmentId Unique string id
     * @param requestingUser User requesting the appointment
     * @param requestedClinician clinician being requested to have the appointment with
     * @param appointmentCategory the generic type of the appointment
     * @param requestedDate date the appointment is requested to be on
     * @param requestDescription a more detailed description of the appointment, possibly including a reason why it was requested
     * @param appointmentStatus the status of the appointment. This can be pending, accepted, rejected, or cancelled.
     */
    public Appointment(String appointmentId, User requestingUser, Clinician requestedClinician, String appointmentCategory, LocalDateTime requestedDate, String requestDescription, String appointmentStatus) {
        this.appointmentId = appointmentId;
        this.requestingUser = requestingUser;
        this.requestedClinician = requestedClinician;
        this.appointmentCategory = appointmentCategory;
        this.requestedDate = requestedDate;
        this.requestDescription = requestDescription;
        this.appointmentStatus = appointmentStatus;
    }

    /**
     * Generate a new unique id for an appointment
     *
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

    public String getAppointmentCategory() {
        return appointmentCategory;
    }

    public void setAppointmentCategory(String appointmentCategory) {
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

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public boolean isHasSeen() {
        return hasSeen;
    }

    public void setHasSeen(boolean hasSeen) {
        this.hasSeen = hasSeen;
    }
}
