package yuown.bulk.service;

import java.io.StringWriter;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import yuown.bulk.entities.Contact;
import yuown.bulk.model.EmailRequest;

@Service
public class MailSenderHelper {

	@Autowired
	private JavaMailSenderImpl javaMailService;

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
		engine.init();
	}

	public void sendMail(final EmailRequest request) {
		prepareMailConfiguration();

		StringResourceRepository repo = (StringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource("template", request.getContent());

		try {
			for (final Contact eachContact : request.getSelectedContacts()) {
				MimeMessage message = javaMailService.createMimeMessage();
				setAuthenticator();
				message.setFrom(fromEmailAddress);
				message.setReplyTo(InternetAddress.parse(configurationService.getByName("mail.reply.to").getStrValue()));
				message.setRecipients(RecipientType.TO, InternetAddress.parse(eachContact.getEmail()));

				message.setSubject(request.getSubject());

				StringWriter writer = processTemplate(eachContact);

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

	public StringWriter processTemplate(final Contact eachContact) {
		VelocityContext context = new VelocityContext();
		replaceMessageTokens(context, eachContact);
		Template template = engine.getTemplate("template");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer;
	}

	public void setAuthenticator() {
		Session.getDefaultInstance(new Properties(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(configurationService.getByName("mail.user.name").getStrValue(), configurationService.getByName("mail.user.pass").getStrValue());
			}
		});
	}

	private void replaceMessageTokens(VelocityContext context, Contact eachContact) {
		context.put("name", eachContact.getName());
	}

	private void prepareMailConfiguration() {
		fromEmailAddress = configurationService.getByName("mail.message.from.default").getStrValue();

		javaMailService.setHost(configurationService.getByName("mail.smtp.host").getStrValue());
		javaMailService.setPort(configurationService.getByName("mail.smtp.port").getValue());
		javaMailService.setUsername(configurationService.getByName("mail.user.name").getStrValue());
		javaMailService.setPassword(configurationService.getByName("mail.user.pass").getStrValue());

		Properties properties = new Properties();
		Boolean isSecured = configurationService.getByName("mail.secured").getBoolValue();
		if (isSecured) {
			properties.put("mail.transport.protocol", "smtps");
			properties.put("mail.smtp.socketFactory.port", configurationService.getByName("mail.smtp.port").getValue());
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		} else {
			properties.put("mail.transport.protocol", "smtp");
		}
		properties.setProperty("mail.smtp.auth", configurationService.getByName("mail.auth.required").getBoolValue().toString());
		properties.setProperty("mail.smtp.starttls.enable", configurationService.getByName("mail.smtp.starttls.enable").getBoolValue().toString());
		properties.setProperty("mail.smtp.user", configurationService.getByName("mail.user.name").getStrValue());
		properties.setProperty("mail.smtp.password", configurationService.getByName("mail.user.pass").getStrValue());
		properties.setProperty("mail.debug", configurationService.getByName("mail.debug").getBoolValue().toString());

		javaMailService.setJavaMailProperties(properties);
	}
}
