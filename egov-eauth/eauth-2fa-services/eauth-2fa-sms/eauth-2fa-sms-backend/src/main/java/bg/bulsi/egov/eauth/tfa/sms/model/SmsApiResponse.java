package bg.bulsi.egov.eauth.tfa.sms.model;

import org.apache.commons.lang3.StringUtils;

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
public class SmsApiResponse {

	private static final String DEFAULT_SEPARATOR = " ";
	@Getter
	@Setter
	private String code;
	
	@Getter
	@Setter
	private String message;
	
	public SmsApiResponse(String response) {
		if (StringUtils.isNotBlank(response)) {
			int index = response.indexOf(DEFAULT_SEPARATOR);
			this.code = response.substring(0, index);
			this.message = response.substring(index + 1);
		}
	}
}
