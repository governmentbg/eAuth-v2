package bg.bulsi.egov.eauth.tfa.totp.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;
import bg.bulsi.egov.eauth.tfa.api.TotpApiDelegate;
import bg.bulsi.egov.eauth.tfa.api.dto.CheckResult;
import bg.bulsi.egov.eauth.tfa.api.dto.SecretMetadata;
import bg.bulsi.egov.eauth.tfa.api.dto.UserIdentityInfo;
import bg.bulsi.egov.eauth.tfa.api.dto.UserInfo;
import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpGeneratedData;
import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpPasswordGeneratorService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TotpService implements TotpApiDelegate {

	private final TotpPasswordGeneratorService passwordGeneratorService;
	private final ApplicationEventPublisher applicationEventPublisher;


	public TotpService(TotpPasswordGeneratorService passwordGeneratorService, ApplicationEventPublisher applicationEventPublisher) {
		this.passwordGeneratorService = passwordGeneratorService;
		this.applicationEventPublisher = applicationEventPublisher;
	}


	@Override
	public ResponseEntity<SecretMetadata> generareNewQR(String applicationID, Optional<String> userID) {

		try {
			TotpGeneratedData data = passwordGeneratorService.generate(userID.orElseThrow(() -> new IllegalArgumentException("missing user")), applicationID);
			SecretMetadata meta = new SecretMetadata()
					.secretKey(data.getSecret())
					.qrImage(TotpPasswordGeneratorService.generateQRCodeImage(data.getGenerateKeyUri(), "PNG", 200, 200));

			return ResponseEntity.ok(meta);

		} catch (Exception e) {

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

	}


	@Override
	public ResponseEntity<UserInfo> generateTOTPKey(UserIdentityInfo body, String applicationID) {
		// TODO Auto-generated method stub
		return TotpApiDelegate.super.generateTOTPKey(body, applicationID);
	}


	@Override
	public ResponseEntity<CheckResult> validateCode(String applicationID, Optional<String> c, Optional<String> u) {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		String code = c.orElseThrow(() -> new IllegalArgumentException("Missing code to verify!"));
		String user = u.orElseThrow(() -> new IllegalArgumentException("Missing user id"));
		try {
			user = URLDecoder.decode(user, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e1) {
			log.error(e1.getMessage(), e1);
		}

		boolean result = passwordGeneratorService.verify(user, code);

		if (result) {
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
					.principal(user)
					.type(EventTypes.VALIDATE_2FA_OTP)
					.data(DataKeys.SOURCE, this.getClass().getName())
					.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);
		}

		return ResponseEntity.ok(new CheckResult().valid(result).status("OK"));
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
