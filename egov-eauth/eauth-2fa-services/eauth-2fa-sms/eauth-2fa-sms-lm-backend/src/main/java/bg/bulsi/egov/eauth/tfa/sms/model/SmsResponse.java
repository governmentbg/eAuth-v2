package bg.bulsi.egov.eauth.tfa.sms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsResponse {

	// meta node is always present with fields: 'code' and 'text'
	JsonNode meta;
	
	// data is NOT always present with all fields
	// if successful fields: 'msg_id', 'sms_id', 'request_id'
	// if NOT only field: 'request_id'
	JsonNode data;
}
