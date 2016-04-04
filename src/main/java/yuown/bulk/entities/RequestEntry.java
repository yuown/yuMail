package yuown.bulk.entities;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "mail_entry", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
@AttributeOverrides(value = {
        @AttributeOverride(name = "id", column = @Column(name = "id", insertable = false, updatable = false)),
        @AttributeOverride(name = "subject", column = @Column(name = "subject")),
        @AttributeOverride(name = "content", column = @Column(name = "content")),
        @AttributeOverride(name = "message", column = @Column(name = "message")),
        @AttributeOverride(name = "status", column = @Column(name = "status")),
        @AttributeOverride(name = "requestId", column = @Column(name = "request_id")),
        @AttributeOverride(name = "name", column = @Column(name = "name"))
})
public class RequestEntry extends BaseEntity<Integer> {

    private String name;

    private String content;

    private String subject;

    private Contact contact;

    private String message;

    private String status;

    private Integer requestId;

    private List<Attachment> attachments;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Lob
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @ManyToOne
    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestEntry other = (RequestEntry) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<Attachment> getAttachments() {
        return attachments;
    }

    @Lob
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }
}
