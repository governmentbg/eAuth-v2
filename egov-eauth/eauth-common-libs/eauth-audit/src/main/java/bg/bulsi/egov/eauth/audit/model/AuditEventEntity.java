package bg.bulsi.egov.eauth.audit.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "audit_event", schema = "public")
public class AuditEventEntity implements Serializable {

	private static final long serialVersionUID = -2607628366244430736L;
	
	
	public AuditEventEntity() {
		//admin-ui test required
	}

	public AuditEventEntity(@NotNull String auditOrigin, @NotNull String principal, @NotNull Instant auditEventDate,
			@NotNull String auditEventType) {
		super();
		this.auditOrigin = auditOrigin;
		this.principal = principal;
		this.auditEventDate = auditEventDate;
		this.auditEventType = auditEventType;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id")
    @Getter
    @Setter
    private Long id;

    @NotNull
    @Column(nullable = false, name = "audit_origin")
    @Getter
    @Setter
    private String auditOrigin;

    @Column(nullable = false, name = "client_ip")
    @Getter
    @Setter
    private String clientIP;    
    
    @NotNull
    @Column(nullable = false)
    @Getter
    @Setter
    private String principal;

    @NotNull
    @Column(name = "event_date")
    @Getter
    @Setter
    private Instant auditEventDate;

    @NotNull
    @Column(name = "event_type")
    @Getter
    @Setter
    private String auditEventType;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "audit_event_data", schema = "public",joinColumns=@JoinColumn(name="event_id"))
    @Getter
    @Setter
    private Map<String, String> data = new HashMap<>();

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuditEventEntity other = (AuditEventEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (auditEventDate == null) {
			if (other.auditEventDate != null)
				return false;
		} else if (!auditEventDate.equals(other.auditEventDate))
			return false;
		if (auditEventType == null) {
			if (other.auditEventType != null)
				return false;
		} else if (!auditEventType.equals(other.auditEventType))
			return false;
		if (auditOrigin == null) {
			if (other.auditOrigin != null)
				return false;
		} else if (!auditOrigin.equals(other.auditOrigin))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((auditEventDate == null) ? 0 : auditEventDate.hashCode());
		result = prime * result + ((auditEventType == null) ? 0 : auditEventType.hashCode());
		result = prime * result + ((auditOrigin == null) ? 0 : auditOrigin.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

    @Override
	public String toString() {
		return String.format(
				"AuditEventEntity [id=%s, auditOrigin=%s, clientIP=%s, principal=%s, auditEventDate=%s, auditEventType=%s, data=%s]",
				id, auditOrigin, clientIP, principal, auditEventDate, auditEventType, data);
	}
}
