package yuown.bulk.entities;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@AttributeOverrides(value = { @AttributeOverride(name = "enabled", column = @Column(name = "ENABLED")) })
public abstract class BaseEntity<ID extends Serializable> {

    public static final String yyyy_MM_dd_HH_mm_ss_S = "yyyy-MM-dd HH:mm:ss.S";

    protected ID id;
    private boolean enabled;

    public BaseEntity() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract ID getId();

    public abstract void setId(ID id);

}
