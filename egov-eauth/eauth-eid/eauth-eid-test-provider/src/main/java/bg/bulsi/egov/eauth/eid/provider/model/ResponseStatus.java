package bg.bulsi.egov.eauth.eid.provider.model;

import lombok.Data;

@Data
public class ResponseStatus {
	
	private boolean failure;
	private String statusCode;
	private String statusMessage;
	private String subStatusCode;

}
