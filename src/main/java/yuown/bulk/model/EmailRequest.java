package yuown.bulk.model;

import yuown.bulk.entities.Contact;

import java.io.Serializable;
import java.util.List;

public class EmailRequest implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -473876435988646341L;

    private List<Contact> selectedContacts;

    private String content;

    private List<String> attachments;
    
    private String subject;

    public List<Contact> getSelectedContacts() {
        return selectedContacts;
    }

    public void setSelectedContacts(List<Contact> selectedContacts) {
        this.selectedContacts = selectedContacts;
    }

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