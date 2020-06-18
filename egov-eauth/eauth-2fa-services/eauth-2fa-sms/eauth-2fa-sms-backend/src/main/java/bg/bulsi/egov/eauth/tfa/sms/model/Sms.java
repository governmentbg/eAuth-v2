package bg.bulsi.egov.eauth.tfa.sms.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "sms", schema = "sms")
public class Sms implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter
	@Setter
	private Long id;
	
	@Getter
	@Setter
	private String phone;
	
	@Getter
	@Setter
	private String code;
	
	@Getter
	@Setter
	private String text;
	
	@Getter
	@Setter
	private String transactionId;
	
	@Getter
	@Setter
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date createDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date editDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Getter
	@Setter
	private Date deliveredDate;
}
