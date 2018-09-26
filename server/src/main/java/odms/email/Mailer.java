package odms.email;

import odms.commons.model.dto.AppointmentWithPeople;
import odms.commons.utils.Log;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class Mailer {


    private MailHandler mailHandler;
    private MessageComposer composer;


    public Mailer(MailHandler mailHandler, MessageComposer composer) {
        this.mailHandler = mailHandler;
        this.composer = composer;
    }

    public Mailer() {
        mailHandler = new MailHandler();
        mailHandler.setMailSender(new JavaMailSenderImpl());
        composer = new MessageComposer();
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
        if (appointment.getUser() != null) {
            if (appointment.getUser().getEmail() == null || appointment.getUser().getEmail().isEmpty()) {
                return;
            }
            switch (statusId) {
                case 2:
                    message = composer.writeAccepted(appointment);
                    break;
                case 3:
                    message = composer.writeRejected(appointment);
                    break;
                case 5:
                    message = composer.writeCancelledByClinician(appointment);
                    break;
                case 6:
                    message = composer.writeUpdated(appointment);
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
}
