package yuown.bulk.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import yuown.bulk.config.MailConfigurator;
import yuown.bulk.entities.Attachment;
import yuown.bulk.entities.Contact;
import yuown.bulk.entities.RequestEntry;
import yuown.bulk.entities.RequestSequence;
import yuown.bulk.model.EmailRequest;
import yuown.bulk.pool.SendMailTask;
import yuown.bulk.repository.AttachmentRepository;
import yuown.bulk.repository.RequestEntryRepository;
import yuown.bulk.repository.RequestSequenceRepository;

@Service
public class MailSenderHelper {

	private AnnotationConfigApplicationContext ctx;

	private VelocityEngine engine;

	private static Logger LOGGER = LoggerFactory.getLogger(MailSenderHelper.class);

	@Autowired
	private SendMailTask asyncMailer;

	@Autowired
	private RequestSequenceRepository requestSequenceRepository;

	@Autowired
	private RequestEntryRepository requestEntryRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	@PostConstruct
	public void init() {
		engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
		engine.setProperty("runtime.log.logsystem.log4j.logger", LOGGER.getName());
		engine.setProperty(Velocity.RESOURCE_LOADER, "string");
		engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
		engine.addProperty("string.resource.loader.repository.static", "false");
		engine.init();

		ctx = new AnnotationConfigApplicationContext();

		ctx.register(MailConfigurator.class);
		ctx.refresh();
	}

	public void sendMail(EmailRequest request) {
		StringResourceRepository repo = (StringResourceRepository) engine.getApplicationAttribute(StringResourceLoader.REPOSITORY_NAME_DEFAULT);
		repo.putStringResource("template", request.getContent());
		RequestSequence seq = new RequestSequence();
		seq = requestSequenceRepository.save(seq);
		try {
			Boolean isProxyEnabled = Boolean.parseBoolean(System.getProperty("proxy.enabled"));
			if (isProxyEnabled) {
				Properties proxySet = System.getProperties();
				if (isProxyEnabled != null && isProxyEnabled) {
					String proxyHost = System.getProperty("proxy.host");
					String proxyPort = System.getProperty("proxy.port");
					proxySet.put("http.proxyPort", proxyPort);
					proxySet.put("http.proxyHost", proxyHost);
					proxySet.put("http.proxySet", "true");
				} else {
					proxySet.put("http.proxySet", "false");
				}
			}
			List<RequestEntry> entries = new ArrayList<RequestEntry>();
			SendMailTask task = ctx.getBean(SendMailTask.class);
			for (final Contact eachContact : request.getSelectedContacts()) {
				StringWriter writer = processTemplate(eachContact);

				RequestEntry entry = new RequestEntry();
				entry.setContact(eachContact);
				entry.setSubject(request.getSubject());
				entry.setContent(writer.toString());
				entry.setAttachments(convert(request.getAttachments()));
				entry.setRequestId(seq.getId());

				entries.add(entry);

				// final CompletableFuture<RequestEntry> entryFinished =
				// task.start();
				// entryFinished.thenRun(new Runnable() {
				// @Override
				// public void run() {
				// attachmentRepository.save(entry.getAttachments());
				// requestEntryRepository.save(entry);
				// }
				// });
			}
			task.setData(entries);
			final CompletableFuture<List<RequestEntry>> entryFinished = task.startBulk();
			entryFinished.thenRun(new Runnable() {
				@Override
				public void run() {
					for (RequestEntry requestEntry : entries) {
						attachmentRepository.save(requestEntry.getAttachments());
						requestEntryRepository.save(requestEntry);
					}
				}
			});
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private List<Attachment> convert(List<String> attachments) {
		List<Attachment> atts = new ArrayList<Attachment>();
		for (String path : attachments) {
			Attachment att = new Attachment();
			att.setPath(path);
			atts.add(att);
		}
		return atts;
	}

	public StringWriter processTemplate(final Contact eachContact) {
		VelocityContext context = new VelocityContext();
		replaceMessageTokens(context, eachContact);
		Template template = engine.getTemplate("template");
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer;
	}

	private void replaceMessageTokens(VelocityContext context, Contact eachContact) {
		context.put("name", eachContact.getName());
	}
}
