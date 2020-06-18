package bg.bulsi.egov.eauth.eid.provider.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "identity", schema = "identity_provider",
uniqueConstraints=@UniqueConstraint(columnNames={"id"}))
public class Identity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private Long id;
	
	@Getter
	@Setter
	private String username;
	
	@Getter
	@Setter
	private String password;
	
	@Getter
	@Setter
	private String nid;
	
	@Getter
	@Setter
	private String names;
	
	@Getter
	@Setter
	private String email;

	@Getter
	@Setter
	private String phone;
	
	@Getter
	@Setter
	private Boolean active;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date editDate;
	
}
