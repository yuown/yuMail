package yuown.bulk.model;

import java.io.Serializable;

public class IdValueModel implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -3340859196299627969L;

    private Integer id;

    private Integer value;

    public IdValueModel() {
    }

    public IdValueModel(Integer id, Integer value) {
        super();
        this.setId(id);
        this.value = value;
    }

    public IdValueModel(Integer id) {
        super();
        this.setId(id);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}