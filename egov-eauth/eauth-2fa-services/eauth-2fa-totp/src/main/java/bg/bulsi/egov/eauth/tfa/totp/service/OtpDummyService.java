package bg.bulsi.egov.eauth.tfa.totp.service;

import bg.bulsi.egov.eauth.tfa.api.OtpApiDelegate;
import bg.bulsi.egov.eauth.tfa.api.dto.AuthRequestDetails;
import bg.bulsi.egov.eauth.tfa.api.dto.CheckResult;
import bg.bulsi.egov.eauth.tfa.api.dto.OTPass;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OtpDummyService implements OtpApiDelegate {

    @Override
    public ResponseEntity<OTPass> clientAuth(AuthRequestDetails body, Optional<String> userID) {
        // TODO Auto-generated method stub
        return OtpApiDelegate.super.clientAuth(body, userID);
    }

    @Override
    public ResponseEntity<CheckResult> validateCode(String c, String t) {
        // TODO Auto-generated method stub
        return OtpApiDelegate.super.validateCode(c, t);
    }

}
