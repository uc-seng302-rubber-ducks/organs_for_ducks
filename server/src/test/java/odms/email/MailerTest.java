package odms.email;

import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model.dto.AppointmentWithPeople;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MailerTest {

    private Mailer testMailer;
    private AppointmentWithPeople appointment;
    private MailHandler mailHandler;
    @Before
    public void setUp(){

        mailHandler = mock(MailHandler.class);
        testMailer = new Mailer();
        testMailer.setMailHandler(mailHandler);
        appointment = new AppointmentWithPeople();
        appointment.setClinician(new Clinician());
        User u = new User();
        u.setEmail("Test@test.com");
        appointment.setUser(u);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d/M/yyyy H m");
        appointment.setAppointmentTime(LocalDateTime.parse("1/1/1990 12 30", dtf));
    }

    @Test
    public void testAcceptedMessageSentOnAccepted(){
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        testMailer.sendAppointmentUpdate(AppointmentStatus.ACCEPTED.getDbValue(), appointment);
        verify(mailHandler).sendMail(anyString(), anyString(), captor.capture());
        Assert.assertTrue(captor.getValue().contains("Has been Accepted"));

    }

    @Test
    public void testUpdatedMessageSentOnUpdated(){
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        testMailer.sendAppointmentUpdate(AppointmentStatus.UPDATED.getDbValue(), appointment);
        verify(mailHandler).sendMail(anyString(), anyString(), captor.capture());
        Assert.assertTrue(captor.getValue().contains("Updated"));

    }

    @Test
    public void testRejectedMessageSentOnRejected(){
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        testMailer.sendAppointmentUpdate(AppointmentStatus.REJECTED.getDbValue(), appointment);
        verify(mailHandler).sendMail(anyString(), anyString(), captor.capture());
        Assert.assertTrue(captor.getValue().contains("not been accepted"));

    }

    @Test
    public void testCancelledMessageSentOnCancelledByClinician(){
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        testMailer.sendAppointmentUpdate(AppointmentStatus.CANCELLED_BY_CLINICIAN.getDbValue(), appointment);
        verify(mailHandler).sendMail(anyString(), anyString(), captor.capture());
        Assert.assertTrue(captor.getValue().contains("cancelled"));

    }

    @Test
    public void testNonMessagingUpdatedSendNoMessages(){
        testMailer.sendAppointmentUpdate(AppointmentStatus.CANCELLED_BY_USER.getDbValue(), appointment);
        testMailer.sendAppointmentUpdate(AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue(), appointment);
        testMailer.sendAppointmentUpdate(AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue(), appointment);
        testMailer.sendAppointmentUpdate(AppointmentStatus.ACCEPTED_SEEN.getDbValue(), appointment);
        testMailer.sendAppointmentUpdate(AppointmentStatus.REJECTED_SEEN.getDbValue(), appointment);
        testMailer.sendAppointmentUpdate(AppointmentStatus.PENDING.getDbValue(), appointment);

        verify(mailHandler, times(0)).sendMail(anyString(), anyString(), anyString());

    }
}
