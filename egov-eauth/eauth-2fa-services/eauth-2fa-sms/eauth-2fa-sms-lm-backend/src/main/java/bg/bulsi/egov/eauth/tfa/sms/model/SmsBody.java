package bg.bulsi.egov.eauth.tfa.sms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({ "msisdn", "shortCode", "text", "serviceId", "priority" })
public class SmsBody {

	private String msisdn;

	@JsonProperty("sc")
	private String shortCode;

	private String text;

	@JsonProperty("service_id")
	private Integer serviceId;

	private String priority;
}
