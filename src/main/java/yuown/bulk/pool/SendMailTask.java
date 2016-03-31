package yuown.bulk.pool;

import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
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

	private List<RequestEntry> entries;

	private static String uid;

	public SendMailTask() {
		uid = UUID.randomUUID().toString();
		System.out.println("SendMailTask Created - " + uid);
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
		System.out.println("Settings Configured to Send Mail - " + uid);
	}

	private void start(RequestEntry entry) {
		prepareMailConfiguration();
		setAuthenticator();
		try {
			System.out.println("Creating Message - " + uid);
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
			System.out.println("Message Successfully Sent - " + uid);
			entry.setStatus(MailStatus.SUCCESS.toString());
		} catch (Exception e) {
			System.out.println("Message Sent Failed - " + uid);
			entry.setMessage(e.getMessage());
			entry.setStatus(MailStatus.FAILED.toString());
			e.printStackTrace();
		}
	}

	public void setAuthenticator() {
		Session.getDefaultInstance(new Properties(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getProperty("mail.user.name"), System.getProperty("mail.user.pass"));
			}
		});
	}

	public void setData(List<RequestEntry> entries) {
		this.entries = entries;
	}

	@Async
	public CompletableFuture<List<RequestEntry>> startBulk() {
		for (RequestEntry entry : entries) {
			start(entry);
		}
		return CompletableFuture.completedFuture(entries);
	}
}
