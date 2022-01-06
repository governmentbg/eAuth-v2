package bg.bulsi.egov.eauth.tfa.sms.service;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import bg.bulsi.egov.eauth.tfa.api.TotpApiDelegate;
import bg.bulsi.egov.eauth.tfa.api.dto.SecretMetadata;
import bg.bulsi.egov.eauth.tfa.api.dto.UserIdentityInfo;
import bg.bulsi.egov.eauth.tfa.api.dto.UserInfo;

@Service
public class TotpDummyService implements TotpApiDelegate {

	@Override
	public ResponseEntity<SecretMetadata> generareNewQR(String applicationID, Optional<String> userID) {
		// TODO Auto-generated method stub
		return TotpApiDelegate.super.generareNewQR(applicationID, userID);
	}

	@Override
	public ResponseEntity<UserInfo> generateTOTPKey(UserIdentityInfo body, String applicationID) {
		// TODO Auto-generated method stub
		return TotpApiDelegate.super.generateTOTPKey(body, applicationID);
	}

	@Override
	public ResponseEntity<Void> logoutUser(String applicationID, String userID, Optional<String> tID) {
		// TODO Auto-generated method stub
		return TotpApiDelegate.super.logoutUser(applicationID, userID, tID);
	}

	@Override
	public ResponseEntity<UserInfo> updateUserData(UserIdentityInfo body, String applicationID) {
		// TODO Auto-generated method stub
		return TotpApiDelegate.super.updateUserData(body, applicationID);
	}

}
