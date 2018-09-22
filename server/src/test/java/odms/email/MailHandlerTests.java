package odms.email;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MailHandlerTests {

    private MailHandler testMailHandler;
    private MailSender mockMailSender;

    @Before
    public void setUp(){
        testMailHandler = new MailHandler();
        mockMailSender = mock(JavaMailSenderImpl.class);
        testMailHandler.setMailSender(mockMailSender);
    }


    @Test
    public void testValidEmailSends(){
        testMailHandler.sendMail("jbu71@uclive.ac.nz", "", "");
        verify(mockMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testInvalidEmailFailsWithoutExceptions(){
        testMailHandler.sendMail("j4856564564", "", "");
        verify(mockMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
