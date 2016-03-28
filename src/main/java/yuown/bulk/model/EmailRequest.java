package yuown.bulk.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import yuown.bulk.entities.Contact;

public class EmailRequest implements Serializable {

	private List<Contact> selectedContacts;

	private String content;

	private List<String> attachments;

	private String subject;

	public EmailRequest() {
	}

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	public List<Contact> getSelectedContacts() {
		return selectedContacts;
	}

	public void setSelectedContacts(List<Contact> selectedContacts) {
		this.selectedContacts = selectedContacts;
	}

	@Lob
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
}