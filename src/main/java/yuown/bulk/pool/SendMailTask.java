package yuown.bulk.pool;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import yuown.bulk.entities.Attachment;
import yuown.bulk.entities.RequestEntry;
import yuown.bulk.model.MailStatus;

@Component
public class SendMailTask {

	@Autowired
	private JavaMailSenderImpl javaMailService;

	private String fromEmailAddress;

	private static final String TEXT_HTML = "text/html; charset=utf-8";

	private RequestEntry entry;

	private String uid;

	private static Logger LOGGER = LoggerFactory.getLogger(SendMailTask.class);

	public SendMailTask() {
		uid = UUID.randomUUID().toString();
		LOGGER.debug("{} - SendMailTask Created", uid);
	}

	private void prepareMailConfiguration() {
		fromEmailAddress = System.getProperty("mail.message.from.default");

		javaMailService.setHost(System.getProperty("mail.smtp.host"));
		javaMailService.setPort(Integer.parseInt(System.getProperty("mail.smtp.port")));
		javaMailService.setUsername(System.getProperty("mail.user.name"));
		javaMailService.setPassword(System.getProperty("mail.user.pass"));

		Properties properties = new Properties();
		Boolean isSecured = Boolean.parseBoolean(System.getProperty("mail.secured"));
		if (isSecured) {
			properties.put("mail.transport.protocol", "smtps");
			properties.put("mail.smtp.socketFactory.port", System.getProperty("mail.smtp.port"));
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		} else {
			properties.put("mail.transport.protocol", "smtp");
		}
		properties.setProperty("mail.smtp.auth", System.getProperty("mail.auth.required"));
		properties.setProperty("mail.smtp.starttls.enable", System.getProperty("mail.smtp.starttls.enable"));
		properties.setProperty("mail.smtp.user", System.getProperty("mail.user.name"));
		properties.setProperty("mail.smtp.password", System.getProperty("mail.user.pass"));
		properties.setProperty("mail.debug", System.getProperty("mail.debug"));

		javaMailService.setJavaMailProperties(properties);
		LOGGER.debug("{} - Settings Configured to Send Mail", uid);
	}

	@Async
	public Future<RequestEntry> start() {
		prepareMailConfiguration();
		setAuthenticator();
		try {
			LOGGER.debug("{} - Creating Message", uid);
			entry.setStatus(MailStatus.STARTED.toString());
			MimeMessage message = javaMailService.createMimeMessage();
			message.setFrom(fromEmailAddress);
			message.setReplyTo(InternetAddress.parse(System.getProperty("mail.reply.to")));
			message.setRecipients(RecipientType.TO, InternetAddress.parse(entry.getContact().getEmail()));

			message.setSubject(entry.getSubject());

			Multipart multipart = new MimeMultipart();

			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(entry.getContent(), TEXT_HTML);

			multipart.addBodyPart(messageBodyPart);

			for (Attachment attachment : entry.getAttachments()) {
				messageBodyPart = new MimeBodyPart();
				DataSource source = new FileDataSource(attachment.getPath());
				messageBodyPart.setDataHandler(new DataHandler(source));
				messageBodyPart.setFileName(source.getName());
				multipart.addBodyPart(messageBodyPart);
			}
			message.setContent(multipart);
			javaMailService.send(message);
			LOGGER.debug("{} - Message Successfully Sent", uid);
			entry.setStatus(MailStatus.SUCCESS.toString());
		} catch (Exception e) {
			LOGGER.debug("{} - Message Sent Failed", uid);
			entry.setMessage(e.getMessage());
			entry.setStatus(MailStatus.FAILED.toString());
			e.printStackTrace();
		}
		return new AsyncResult<RequestEntry>(entry);
	}

	public void setAuthenticator() {
		Session.getDefaultInstance(new Properties(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getProperty("mail.user.name"), System.getProperty("mail.user.pass"));
			}
		});
	}

	public void setRequestEntry(RequestEntry entry) {
		this.entry = entry;
	}
}
