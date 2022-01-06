package bg.bulsi.egov.eauth.tfa.sms.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.tfa.api.OtpApiDelegate;
import bg.bulsi.egov.eauth.tfa.api.dto.AuthRequestDetails;
import bg.bulsi.egov.eauth.tfa.api.dto.CheckResult;
import bg.bulsi.egov.eauth.tfa.api.dto.OTPass;
import bg.bulsi.egov.eauth.tfa.sms.data.SmsRepository;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsLM;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsResponse;
import bg.bulsi.egov.eauth.tfa.sms.model.SmsStatus;
import bg.bulsi.egov.eauth.tfa.util.HashUtils;
import bg.bulsi.egov.eauth.tfa.util.SmsUtils;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SmsService implements OtpApiDelegate {

	private static final String SMS_PREFIX = "+359";

	private static final String SMS_OTP_EXP_KEY = "egov.eauth.dyn.tfa.sms.otp.expiration";
	
	private final SmsStoreService store;


	@Autowired
	public SmsService(SmsStoreService store) {
		this.store = store; // init store
	}


	@Autowired
	private SmsRepository repository;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HazelcastService hazelcastService;

	// TODO: change in DB!!!
	@Value("${egov.eauth.sys.tfa.sms.provider.api.url}")
	private String apiUrl;

	@Value("${egov.eauth.sys.tfa.sms.provider.api.key}")
	private String apiKey;
	
	// TODO: add in DB!!!
	@Value("${egov.eauth.sys.tfa.sms.provider.api.secret:$2y$10$Ny.07AJ5mJRxaY55BuT.KeQeGZoDaq3NJ0Pg.uSosj4zoaEGQvwAC}")
	private String apiSecret;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;


	public SmsLM create(SmsLM sms) {
		return repository.save(sms);
	}


	public SmsLM update(SmsLM sms) {
		return repository.save(sms);
	}


	public List<SmsLM> findAllSmsCreatedBefore(Date createDate) {
		return repository.findAllWithCreateDateBefore(createDate);
	}


	// must be annotated because of DML
	@Transactional
	public void deleteAllSmsCreatedBefore(Date createDate) {
		repository.deleteAllWithCreateDateBefore(createDate);
	}


	public Optional<SmsLM> findSmsById(Long id) {
		return repository.findById(id);
	}


	public Optional<SmsLM> findSmsByTransactionIdAndCode(String transactionId, String code) {
		return repository.findByTransactionIdAndCode(transactionId, code);
	}


	public boolean validateExpired(String transactionId, String code) {

		Optional<SmsLM> sms = findSmsByTransactionIdAndCode(transactionId, code);
		long expirationTimeInSeconds = Long.parseLong(hazelcastService.get(SMS_OTP_EXP_KEY));

		/*
		 * AuditEvent
		 */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.EXPIRED_2FA_OTP)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);

		return sms.isPresent() && SmsUtils.codeNotExpired(sms.get().getCreateDate(), expirationTimeInSeconds);
	}


	public SmsResponse send(SmsLM sms) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String smsAsJson = mapper.writeValueAsString(sms);
		log.info("smsAsJson: [{}]", smsAsJson);
		
		String hashedData = HashUtils.sha512Hash(apiSecret, smsAsJson);
		log.info("hashedData: [{}]", hashedData);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("x-api-key", apiKey);
		headers.set("x-api-sign", hashedData);
		headers.set("Expect", ""); // empty
		
		HttpEntity<String> request = new HttpEntity<>(smsAsJson, headers);
		
		ResponseEntity<SmsResponse> response = restTemplate.postForEntity(apiUrl, request, SmsResponse.class);
		log.info("response: [{}]", response);

		return response.getBody();
	}


	@Override
	public ResponseEntity<OTPass> clientAuth(AuthRequestDetails body, Optional<String> userID) {
		
		ResponseEntity<OTPass> responseEntity = null;
		String phone = body.getContacts().getPhone();
		log.info("phone: [{}]", phone);

		if (StringUtils.isBlank(phone) || !phone.trim().startsWith(SMS_PREFIX)) {
			log.error("Bad phone formatt: [{}]", phone);
			return ResponseEntity.status(500).body(new OTPass());
		}

		SmsLM sms = create(phone);
		store.save(sms);

		try {
			SmsResponse apiResponse = send(sms);
			int code = apiResponse.getData().get("code").asInt();

			if (code == 200) {
				// update status
				sms.setStatus(SmsStatus.SENT.name());
				sms.setEditDate(new Date());
				update(sms);
				store.update(sms);

				long expirationTimeInSeconds = Long.parseLong(hazelcastService.get(SMS_OTP_EXP_KEY));
				responseEntity = ResponseEntity.ok(new OTPass().transaction(sms.getTransactionId())
						.timestamp(sms.getCreateDate().getTime() + (expirationTimeInSeconds * 1000)).complete(true));
				
				/*
				 * AuditEvent
				 */
				AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
						.type(EventTypes.SEND_2FA_OTP)
						.data(DataKeys.SOURCE, this.getClass().getName())
						.build();
				applicationEventPublisher.publishEvent(auditApplicationEvent);
			}

		} catch (Exception ex) {
			long expirationTimeInSeconds = Long.parseLong(hazelcastService.get(SMS_OTP_EXP_KEY));
			responseEntity = ResponseEntity.status(500).body(new OTPass().transaction(sms.getTransactionId())
					.timestamp(sms.getCreateDate().getTime() + (expirationTimeInSeconds * 1000)).complete(false));
			log.error(ex.getCause().getLocalizedMessage());
		}

		return responseEntity;
	}


	@Override
	public ResponseEntity<CheckResult> validateCode(String code, String transactionId) {

		long expirationTimeInSeconds = Long.parseLong(hazelcastService.get(SMS_OTP_EXP_KEY));

		boolean valid = store.validate(transactionId, code, expirationTimeInSeconds);
		log.info("valid: [{}]", valid);

		CheckResult result = new CheckResult().valid(valid).status("OK").validUntil(0L);

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


	private SmsLM create(String phone) {
		String template = hazelcastService.get("egov.eauth.dyn.tfa.sms.provider.text.template");
		SmsLM sms = new SmsLM();
		sms.setPhone(phone);
		sms.setCode(SmsUtils.generateCode());
		sms.setText(template + sms.getCode());
		sms.setTransactionId(SmsUtils.generateTransactionId());
		sms.setStatus(SmsStatus.CREATED.name());
		sms.setCreateDate(new Date());

		return repository.save(sms);
	}
}
