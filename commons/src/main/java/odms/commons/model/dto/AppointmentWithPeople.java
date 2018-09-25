package odms.commons.model.dto;

import odms.commons.model.Clinician;
import odms.commons.model.User;

import java.time.LocalDateTime;

/**
 * joins an appointment with a user and clinician
 */
public class AppointmentWithPeople {
    User user;
    Clinician clinician;
    LocalDateTime appointmentTime;

    public AppointmentWithPeople() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Clinician getClinician() {
        return clinician;
    }

    public void setClinician(Clinician clinician) {
        this.clinician = clinician;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentDateTimeString(){
        return appointmentTime.toLocalTime().toString().replaceAll("T", " ");
    }
}
