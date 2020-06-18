package bg.bulsi.egov.eauth.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "_user_profile", schema = "profile",
	uniqueConstraints=@UniqueConstraint(columnNames={"person_id"})
		)
@EqualsAndHashCode(callSuper = false)
@DynamicInsert
@DynamicUpdate
public class User implements Persistable<Long>, Serializable {

	private static final long serialVersionUID = -7181382246285301337L;

	public static final String TOTP_SECRET = "TOTP_SECRET";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
	@SequenceGenerator(name = "user_generator", sequenceName = "user_seq", allocationSize = 1, schema = "profile")
	@Getter
	@Setter
	private Long id;
	
	@Getter
	@Setter
	@ElementCollection
	@MapKeyColumn(name = "key")
	@Column(name = "value")
	@CollectionTable(name = "additional_attributes", schema = "profile", joinColumns = @JoinColumn(name = "profile_id"))
	private Map<String, String> attributes = new HashMap<>(); // maps from attribute name to value

	@NotBlank
	@Getter
	@Setter
	@Column(name = "person_id", length = 255)
	private String personID;
	
	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	@Column(name = "preferred_method", columnDefinition = "varchar(10) default 'EMAIL'")
	private Preferred2FA preferred;

	@NotBlank
	@Getter
	@Setter
	private String name;

	@Size(max = 40)
	@Email
	@Getter
	@Setter
	private String email;

	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@NotEmpty
	@Getter
	@Setter
	@Size(max = 20)
	@Column(name = "phone_number")
	private String phoneNumber;

	@Getter
	@Setter
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", referencedColumnName = "id")
	private Address address;


	public User() {
		this(null, null);
	}

	@Override
	public boolean isNew() {
		return getId() == null;
	}


}
