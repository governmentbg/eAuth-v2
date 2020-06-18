package bg.bulsi.egov.eauth.tfa.sms.model;

import java.io.Serializable;

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
public class SmsValidRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private String transactionId;
	
	@Getter
	@Setter
	private String code;
	
}