package odms.email;

import odms.commons.model.dto.AppointmentWithPeople;
import odms.commons.utils.Log;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;


@Component
public class Scheduler {

    private JDBCDriver driver;
    private DBHandler handler;
    private MessageComposer composer;


    @Autowired
    public Scheduler(DBManager dbManager, MessageComposer composer) {
        this.driver = dbManager.getDriver();
        this.handler = dbManager.getHandler();
        this.composer = composer;
    }


    /**
     * Runs this function on schedule at * am every morning.
     * note: spring boot uses six field cron instead of 5 field
     * so we have second, minute, hour, day-of-month, month, day-of-year, year with year being optional
     * for more info visit https://www.baeldung.com/cron-expressions
     */
    @Scheduled(cron = "0 0 8 * * * ")
    public void sendEmailsDaily() {
        Collection<AppointmentWithPeople> appointmentWithPeopleTomorrow;
        Collection<AppointmentWithPeople> appointmentWithPeopleNextWeek;
        Collection<AppointmentWithPeople> appointments = new ArrayList<>();
        try (Connection connection = driver.getConnection()) {
            appointmentWithPeopleTomorrow = handler.getAppointmentsForDate(connection, LocalDate.now().plusDays(1));
            appointmentWithPeopleNextWeek = handler.getAppointmentsForDate(connection, LocalDate.now().plusDays(7));
            appointments.addAll(appointmentWithPeopleNextWeek);
            appointments.addAll(appointmentWithPeopleTomorrow);

        } catch (SQLException e) {
            Log.severe("Unable to get appointments date", e);
        }
        MailHandler mailHandler = new MailHandler();
        mailHandler.setMailSender(new JavaMailSenderImpl() {
        });
        for (AppointmentWithPeople appointment : appointments) {
            if (!appointment.getUser().getEmail().equals("") || appointment.getUser().getEmail() != null) {
                String message = composer.writeReminder(appointment);
                if (!message.isEmpty()) {
                    mailHandler.sendMail(appointment.getUser().getEmail(),
                            String.format("Appointment for: %s", appointment.getAppointmentDateTimeString()),
                            message);
                }
            }
        }

    }
}
