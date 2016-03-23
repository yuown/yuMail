package yuown.bulk.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Contact;
import yuown.bulk.model.EmailRequest;

import java.io.StringWriter;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Service
public class MailSenderHelper {

    @Autowired
    private JavaMailSender javaMailService;

    @Autowired
    private VelocityEngine velocityEngine;
    
    @Autowired
    private ConfigurationService configurationService;

    private String fromEmailAddress;

    private VelocityEngine engine;

    private static final String TEXT_HTML = "text/html; charset=utf-8";

    private static Logger LOGGER = LoggerFactory.getLogger(MailSenderHelper.class);

    @PostConstruct
    public void init() {
        engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        engine.setProperty("runtime.log.logsystem.log4j.logger", LOGGER.getName());
        engine.setProperty(Velocity.RESOURCE_LOADER, "string");
        engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        engine.addProperty("string.resource.loader.repository.static", "false");
        //  engine.addProperty("string.resource.loader.modificationCheckInterval", "1");
        engine.init();
    }

    public void sendMail(final EmailRequest request) {
        fromEmailAddress = configurationService.getByName("mail.message.from.default").getStrValue();
        
        StringResourceRepository repo = (StringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
        repo.putStringResource("template", request.getContent());
        try {
            for (final Contact eachContact : request.getSelectedContacts()) {
                MimeMessage message = javaMailService.createMimeMessage();
                message.setFrom(fromEmailAddress);
                addRecipients(eachContact, message, Message.RecipientType.TO, "TO");

                message.setSubject(request.getSubject());

                VelocityContext context = new VelocityContext();
                replaceMessageTokens(context, eachContact);
                Template template = engine.getTemplate("template");
                StringWriter writer = new StringWriter();
                template.merge(context, writer);

                Multipart multipart = new MimeMultipart();

                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent(writer.toString(), TEXT_HTML);

                multipart.addBodyPart(messageBodyPart);

                for (String attachment : request.getAttachments()) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachment);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(source.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
                message.setContent(multipart);
                javaMailService.send(message);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void replaceMessageTokens(VelocityContext context, Contact eachContact) {
        context.put("name", eachContact.getName());
    }

    private void addRecipients(Contact contact, MimeMessage message, Message.RecipientType mimeRecipientType, String recipientType) throws MessagingException, AddressException {
        String recipient = contact.getEmail();
        if (StringUtils.isNotBlank(recipient)) {
            message.setRecipients(mimeRecipientType, InternetAddress.parse(recipient));
        }
    }
}
