package bg.bulsi.egov.eauth.tfa.email.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;
import bg.bulsi.egov.eauth.tfa.api.OtpApiDelegate;
import bg.bulsi.egov.eauth.tfa.api.dto.AuthRequestDetails;
import bg.bulsi.egov.eauth.tfa.api.dto.CheckResult;
import bg.bulsi.egov.eauth.tfa.api.dto.OTPass;
import bg.bulsi.egov.eauth.tfa.email.services.email.AsyncEmailService;
import bg.bulsi.egov.eauth.tfa.email.services.email.MailContent;
import bg.bulsi.egov.eauth.tfa.email.utils.TwoFAToken;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OtpService implements OtpApiDelegate {

	private final TokenService tokenService;
	private final AsyncEmailService asyncEmailService;
	private final HazelcastService hazelcastService;
	private ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	public OtpService(TokenService tokenService, AsyncEmailService asyncEmailService, HazelcastService hazelcastService, ApplicationEventPublisher applicationEventPublisher) {
		this.tokenService = tokenService;
		this.asyncEmailService = asyncEmailService;
		this.hazelcastService = hazelcastService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public ResponseEntity<OTPass> clientAuth(AuthRequestDetails body, Optional<String> userID) {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		TwoFAToken token = tokenService.generate(body.getContacts().getEmail());

		final String subject = hazelcastService.get("egov.eauth.dyn.tfa.email.sender.subject");

		MailContent content = createMailContent().subject(subject)
				.to(body.getContacts().getEmail());

		content.template().templateName("SecurityCodeMail.html")
				.property("Subject", subject).property("Code", token.getCode());

		boolean isValid = sendEmail(content);

		if (isValid) {
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
					.principal(subject)
					.type(EventTypes.SEND_2FA_OTP)
					.data(DataKeys.SOURCE, this.getClass().getName())
					.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);
		} else {
			tokenService.removeToken(token.getTransactionId());
			ResponseEntity.status(500).body(new OTPass().transaction(token.getTransactionId())
					.timestamp(token.getTimeStamp()).complete(false));
		}

		return ResponseEntity.ok(new OTPass().transaction(token.getTransactionId())
				.timestamp(token.getTimeStamp() + (Integer.parseInt(hazelcastService.get("egov.eauth.dyn.tfa.email.otp.expiration")) * 1000)).complete(true));
	}


	@Override
	public ResponseEntity<CheckResult> validateCode(String c, String t) {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		boolean valid = tokenService.validateToken(t, c);

		CheckResult result = new CheckResult()
				.valid(valid)
				.status("OK")
				.validUntil(0L);

		if (valid) {
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
					.type(EventTypes.VALIDATE_2FA_OTP)
					.data(DataKeys.SOURCE, this.getClass().getName())
					.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);
		}

		return ResponseEntity.ok(result);
	}


	private MailContent createMailContent() {
		return new MailContent().from(hazelcastService.get("egov.eauth.sys.tfa.email.sender.from"));
	}


	private boolean sendEmail(MailContent content) {
		try {
			Future<String> stringFuture = asyncEmailService.sendMail(content);
			stringFuture.get(30, TimeUnit.SECONDS);
			log.info("Email sended to {}", content.to());
		} catch (InterruptedException | ExecutionException | TimeoutException ex) {
			log.error(ex.getCause().getLocalizedMessage());
			return false;
		} catch (Exception ex) {
			if (ex.getCause() instanceof MailSendException) {
				log.info(ex.getCause().getLocalizedMessage());
			} else {
				log.error("Problem Sending email: {}\n{}", ex.getCause().getLocalizedMessage(), ex.getCause());
			}
			return false;
		}
		return true;
	}
}
