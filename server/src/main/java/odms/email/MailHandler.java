package odms.email;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class MailHandler {
    private static final String EMAIL_ADDRESS = "organsforducks@gmail.com";
    private MailSender mailSender;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, String subject, String msg) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(EMAIL_ADDRESS);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        mailSender.send(message);
    }
}
