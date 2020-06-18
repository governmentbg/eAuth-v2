package bg.bulsi.egov.idp.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EnabledOTPMethod implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private OTPMethod method;

	@Getter
	@Setter
	private Boolean defaultMethod = false;
}
