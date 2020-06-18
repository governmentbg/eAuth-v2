package bg.bulsi.egov.eauth.eid.provider.model;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import lombok.Getter;
import lombok.Setter;

public class UserStatusDataIn {
	
	@Getter
	@Setter
	private AuthenticationRequest authenticationRequest;
	
	@Getter
	@Setter
	private String intermediateStatus;

}
