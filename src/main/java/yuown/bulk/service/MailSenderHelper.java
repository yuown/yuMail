package yuown.bulk.service;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Contact;
import yuown.bulk.model.EmailRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

@Service
public class MailSenderHelper {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    private String fromEmailAddress;

    private RuntimeServices runtimeServices;

    @PostConstruct
    public void init() {

    }

    public void sendMail(final EmailRequest request) {
        runtimeServices = RuntimeSingleton.getRuntimeServices();
        StringReader reader = new StringReader(fromEmailAddress);
        try {
            SimpleNode node = runtimeServices.parse(reader, "Template name");
            Template template = new Template();
            template.setRuntimeServices(runtimeServices);
            template.setData(node);
            template.initDocument();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        for (final Contact eachContact : request.getSelectedContacts()) {
            MimeMessagePreparator preparator = new MimeMessagePreparator() {

                @Override
                public void prepare(MimeMessage mimeMessage) throws Exception {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(eachContact.getEmail());
                    message.setFrom(fromEmailAddress);
                    Map model = new HashMap();
                    model.put("name", eachContact.getName());
                    velocityEngine.e
                }

            };
        }
    }
}
