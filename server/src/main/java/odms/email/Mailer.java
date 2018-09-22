package odms.email;

import odms.commons.model.dto.AppointmentWithPeople;
import odms.commons.utils.Log;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static java.lang.String.format;

public class Mailer {

    private static final String ACCEPTED_MESSAGE = "Hello %s\n\nYour appointment scheduled for %s,\n" +
            "With %s %s, Has been Accepted\nIt is now at %s\n\n\nRegards,\n\n%s %s";
    private static final String CANCELLED_BY_CLINICIAN_MESSAGE = "Hello %s\n\nYour appointment scheduled for %s," +
            "\nWith %s %s, Has been cancelled. We apologise for any inconvenience this has caused\n\n\nRegards,\n\n%s %s";
    private static final String REJECTED_MESSAGE = "Hello %s\n\nYour appointment scheduled for %s," +
            "\nWith %s %s, Has not been accepted. Please make a new appointment\n\n\nRegards,\n\n%s %s";
    private static final String UPDATED_MESSAGE = "Hello %s\n\nYour appointment with %s %s, Has been Updated\n" +
            "Your new Appointment is Scheduled for %s, If this does not work for you please contact us or schedule a new appointment\n\n\nRegards,\n\n%s %s";
    private MailHandler mailHandler;


    public Mailer(MailHandler mailHandler) {
        this.mailHandler = mailHandler;
    }

    public Mailer() {
        mailHandler = new MailHandler();
        mailHandler.setMailSender(new JavaMailSenderImpl());
    }

    public MailHandler getMailHandler() {
        return mailHandler;
    }

    public void setMailHandler(MailHandler mailHandler) {
        this.mailHandler = mailHandler;
    }

    /**
     * Sends the correct message to a user based on the status that is provided
     *
     * @param statusId    status of the appointment
     * @param appointment appointment information to update about
     */
    public void sendAppointmentUpdate(int statusId, AppointmentWithPeople appointment) {
        String message;
        if (appointment.getUser().getEmail() == null || appointment.getUser().getEmail().isEmpty()) {
            return;
        }
        switch (statusId) {
            case 2:
                message = format(ACCEPTED_MESSAGE,
                        appointment.getUser().getFullName(),
                        appointment.getAppointmentTime().toLocalDate().toString(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName(),
                        appointment.getAppointmentTime().toLocalTime().toString(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName());
                break;
            case 3:
                message = format(REJECTED_MESSAGE,
                        appointment.getUser().getFullName(),
                        appointment.getAppointmentTime().toLocalDate().toString(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName());
                break;
            case 5:
                message = format(CANCELLED_BY_CLINICIAN_MESSAGE,
                        appointment.getUser().getFullName(),
                        appointment.getAppointmentTime().toLocalDate().toString(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName());
                break;
            case 6:
                message = format(UPDATED_MESSAGE,
                        appointment.getUser().getFullName(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName(),
                        appointment.getAppointmentDateTimeString(),
                        appointment.getClinician().getFirstName(),
                        appointment.getClinician().getLastName());
                break;
            default:
                message = "";
        }
        if (message.isEmpty()) {
            return;
        }
        Log.info("Sending\n" + message);
        String subject = "Appointment With " + appointment.getClinician().getFirstName() + " " + appointment.getClinician().getLastName();
        mailHandler.sendMail(appointment.getUser().getEmail(), subject, message);
    }
}
