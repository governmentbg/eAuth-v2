package bg.eauth.admin.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import bg.eauth.admin.config.audit.ConfigEntityAuditListener;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A Config.
 */
@Entity
@Table(name = "db_property_source", schema = "public")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@EntityListeners(ConfigEntityAuditListener.class)
public class Config implements Serializable {

    private static final long serialVersionUID = 1L;

//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//    @SequenceGenerator(name = "sequenceGenerator")
//    private Long id;

    @Id
    @Column(name = "property_key")
    private String propertyKey;

    @Column(name = "property_value")
    private String propertyValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public Config propertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
        return this;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public Config propertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof Config)) {
//            return false;
//        }
//        return id != null && id.equals(((Config) o).id);
//    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Config{" +
//            "id=" + getId() +
            ", propertyKey='" + getPropertyKey() + "'" +
            ", propertyValue='" + getPropertyValue() + "'" +
            "}";
    }
}
