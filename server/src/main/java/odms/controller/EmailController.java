package odms.controller;

import odms.email.MailHandler;
import odms.utils.DBManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@OdmsController
public class EmailController extends BaseController {

    public EmailController(DBManager manager) {
        super(manager);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/email")
    public void sendEmail(){
        MailHandler mailHandler = new MailHandler();
        mailHandler.setMailSender(new JavaMailSenderImpl() {
        });
        mailHandler.sendMail("organsforducks@gmail.com", "", "test","Test");
    }
}
