package bg.bulsi.egov.eauth.eid.provider.model;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.CommonAuthException;
import lombok.Getter;
import lombok.Setter;

public class UserStatusDataOut {
	
	@Getter
	@Setter
	private AuthenticationResponse authenticationResponse;
	
	@Getter
	@Setter
	private CommonAuthException intermediateStatus;

}
