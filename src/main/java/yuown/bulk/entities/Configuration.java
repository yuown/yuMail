package yuown.bulk.entities;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "CONFIGURATION", uniqueConstraints = @UniqueConstraint(columnNames = { "id" }))
@AttributeOverrides( value = 
{
    @AttributeOverride(name = "id", column = @Column(name = "ID", insertable = false, updatable = false)),
    @AttributeOverride(name = "name", column = @Column(name = "name")),
    @AttributeOverride(name = "value", column = @Column(name = "value")),
    @AttributeOverride(name = "strValue", column = @Column(name = "str_value")),
    @AttributeOverride(name = "boolValue", column = @Column(name = "bool_value")),
    @AttributeOverride(name = "autoLoad", column = @Column(name = "load_on_startup")),
    @AttributeOverride(name = "deletable", column = @Column(name = "deletable"))
})
public class Configuration extends BaseEntity<Integer> implements Serializable {

    private static final long serialVersionUID = 4289151143888117381L;

    private String name;
    
    private String description;
    
    private Integer value;
    
    private String strValue;
    
    private Boolean boolValue;
    
    private Boolean autoLoad;
    
    private Boolean deletable;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getStrValue() {
		return strValue;
	}

	public void setStrValue(String strValue) {
		this.strValue = strValue;
	}

	public Boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getAutoLoad() {
		return autoLoad;
	}

	public void setAutoLoad(Boolean autoLoad) {
		this.autoLoad = autoLoad;
	}

	public Boolean getDeletable() {
        return deletable;
    }

    public void setDeletable(Boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public int hashCode() {
        return (new HashCodeBuilder()).append(this.id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Configuration rhs = (Configuration) obj;
        return (new EqualsBuilder()).append(this.id, rhs.id).isEquals();
    }
}
