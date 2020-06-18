package bg.bulsi.egov.eauth.eid.provider.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Enumeration;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.eid.ExIdentApiDelegate;
import bg.bulsi.egov.eauth.eid.dto.AttributeMap;
import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.IdentityData;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.eid.dto.ProcessingData;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.eauth.eid.dto.UserData;
import bg.bulsi.egov.eauth.eid.provider.cash.Cache;
import bg.bulsi.egov.eauth.eid.provider.cash.InMemoryCache;
import bg.bulsi.egov.eauth.eid.provider.model.AttributeMapKeys;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import bg.bulsi.egov.eauth.eid.provider.model.UserStatusDataIn;
import bg.bulsi.egov.eauth.eid.provider.model.UserStatusDataOut;
import bg.bulsi.egov.eauth.eid.provider.model.repository.IdentityRepository;
import bg.bulsi.egov.eauth.eid.provider.service.exception.EidProviderException;
import bg.bulsi.egov.eauth.eid.provider.utils.AutorizeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EidService implements ExIdentApiDelegate {

	@Value("${eid.provider.identity.type}")
	private String defaultIdentity;

	/*
	 * String relyingPartyRequestID UserStatusData RAWin
	 */
	private static Cache<UserStatusDataIn> identitiyRequiestCash = new InMemoryCache<>();
	/*
	 * String relyingPartyRequestID UserStatusData out
	 */
	private static Cache<UserStatusDataOut> identitiyResponseCash = new InMemoryCache<>();

	private String authKey = "6Uh2ji5GXIk8Deiws57fXBNYtWzQHXI9noqtcykhE3I=";

	@Autowired
	private IdentityRepository identityRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public EidService() {
		// FIXME CALCULATE authKey dynamically

		// 6Uh2ji5GXIk8Deiws57fXBNYtWzQHXI9noqtcykhE3I=
		// authKey = AutorizeKey.encodeHmacSHA256("fullAuthenticationRequest","vendorKey");

		// this.authKey = getAutKey(body);
	
	}



	/**
	 * Use temporary String authorizeKey =
	 * "32edfe308b6a5c627fdef0cab411a79ab1a0a73b19fae24d7663cf33dca1863a"; for
	 * authentication
	 * 
	 * @ApiResponse(code = 200, message = "Request accepted successfuly", response =
	 *                   InquiryResult.class),
	 * @ApiResponse(code = 400, message = "Invalid data supplied"),
	 * @ApiResponse(code = 401, message = "API key is missing or invalid"),
	 * @ApiResponse(code = 405, message = "Invalid request"),
	 * @ApiResponse(code = 454, message = "Incorrect coverage")
	 */
	@SuppressWarnings({ "unused" })
	@Override
	public ResponseEntity<InquiryResult> identityInquiry(AuthenticationRequest body) throws EidProviderException{

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		
		ProcessingData processingData = body.getProcessing();

		Optional<Identity> identityOpt = findIdentity(body);
		Identity identity;
		String id = null;

		if (identityOpt.isPresent()) {
			identity = identityOpt.get();
			id = identity.getNid();
		} else {
			/*
			 * @Disabled publishing AuditEvent
			 */
			@SuppressWarnings("unused")
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.TEST_EID_AUTHN_FAILED)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
			// applicationEventPublisher.publishEvent(auditApplicationEvent);

			throw new EidProviderException("Not present", 405);
		}

		String relyingPartyRequestID = generateRelyingParty(id);

		OffsetDateTime validityDateTime;
		if (true) {			
			validityDateTime = processingData.getResponceTimeout();
		} else {
			int expPeriod = getExpPeriod(body);
			validityDateTime = OffsetDateTime.ofInstant(Instant.now().plusSeconds(expPeriod),ZoneId.systemDefault());
		}
		
		if (AuthProcessingType.POLLING.equals(processingData.getPtype())) {
		
			/*
			 * Add in IN HashMap
			 */
			addRequestMap(relyingPartyRequestID, body, HttpStatus.valueOf(200), validityDateTime);

		} else if (AuthProcessingType.CALLBACK.equals(processingData.getPtype())) {
			
			if (processingData.getResponceTimeout().toInstant().toEpochMilli() < OffsetDateTime.now().toInstant().toEpochMilli()) {
				
				//XXX Read code
				HttpStatus code = HttpStatus.ACCEPTED;
				
				AuthenticationCallbackResult authenticationCallbackResult = genAuthCallBackResponse(relyingPartyRequestID, body, identity, code);

				// TODO CALLBACK URL
			
			}
			
		}
		

		InquiryResult inq = new InquiryResult();
		inq.setRelyingPartyRequestID(relyingPartyRequestID);
		inq.setValidity(validityDateTime);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		//TODO get from configuration 3rd.idp#provider-api-key
		headers.set("api_key", "12345");

		/*
		 * @Disabled publishing AuditEvent
		 */
		@SuppressWarnings("unused")
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.TEST_EID_AUTHN_SUCCESS)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		// applicationEventPublisher.publishEvent(auditApplicationEvent);

		return ResponseEntity.status(HttpStatus.OK).headers(headers).body(inq);
		// return new ResponseEntity<>(inq, HttpStatus.OK);
	}

	/*
	 * Get expiration period from Additional attributes in SEC
	 */
	@Deprecated
	private int getExpPeriod(AuthenticationRequest body) {
		Set<String> keys = body.getIdentificationAttributes().keySet();
		String expPeriodKey = null;
		for (String string : keys) {
			if (string.contains("EXPIRATIONPERIOD")) {
				expPeriodKey = string;
			}
		}

		int expPeriod = 0;
		if (expPeriodKey != null) {
			try {
				String expPeriodAttributeValue = body.getIdentificationAttributes().get(expPeriodKey);
				expPeriod = Integer.parseInt(expPeriodAttributeValue);
			} catch (NumberFormatException e) {
				expPeriod = 0;
			}
		}
		return expPeriod;
	}

	private Optional<Identity> findIdentity(AuthenticationRequest body) {
		UserAuthData userAuthData = body.getUser();
		String id = userAuthData.getIdentityString();

		Optional<Identity> identityOpt = Optional.empty();

		// FIXME Change identity type with some Enum
		if ("egn".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByNid(id);
		} else if ("email".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByEmail(id);
		} else if ("phone".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByPhone(id);
		} else {
			log.error("Missing IDENTITY TYPE @findIdentity");
		}

		return identityOpt;
	}

	/**
	 * 
	 * @ApiResponse(code = 200, message = "successful operation", response =
	 *                   AuthenticationResponse.class),
	 * @ApiResponse(code = 400, message = "Invalid status value"),
	 * @ApiResponse(code = 401, message = "API key is missing or invalid"),
	 * @ApiResponse(code = 438, message = "Issuer not found"),
	 * @ApiResponse(code = 405, message = "Invalid request"),
	 * @ApiResponse(code = 451, message = "Responce Not Ready", response =
	 *                   ResponseStatus.class)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResponseEntity getAuthentication(String relyingPartyRequestID) {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();

		UserStatusDataOut userStatusDataOut;
		AuthenticationResponse authenticationResponse;

		userStatusDataOut = identitiyResponseCash.get(relyingPartyRequestID);
		identitiyResponseCash.remove(relyingPartyRequestID);

		authenticationResponse = userStatusDataOut.getAuthenticationResponse();
		if (authenticationResponse != null) {
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
						.type(EventTypes.TEST_EID_AUTHN_SUCCESS)
						.data(DataKeys.SOURCE, this.getClass().getName())
						.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);

			return new ResponseEntity<>(authenticationResponse, HttpStatus.valueOf(200));
		} else {
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
						.type(EventTypes.TEST_EID_AUTHN_FAILED)
						.data(DataKeys.SOURCE, this.getClass().getName())
						.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);

			throw new EidProviderException("Invalid request", 405);
		}

	}

	/**
	 * Read from RequestMap Write to ResponseMap and Remove from RequestMap
	 *
	 * @return
	 */
	@Scheduled(initialDelay = 15000, fixedRate = 3000)
	public void poolInMemoryToIdentityInquiry() {

		Optional<String> keyOpt = identitiyRequiestCash.getOldestKey();

		UserStatusDataIn reqData = null;
		long reqExp;
		if (keyOpt.isPresent()) {
			String key = keyOpt.get();

			reqData = identitiyRequiestCash.get(key);
			reqExp = identitiyRequiestCash.getExpiredTime(key);

			AuthenticationRequest authenticationRequest = reqData.getAuthenticationRequest();
			// ResponseStatus status = reqData.getStatus();

			Optional<Identity> identityOpt = findIdentity(authenticationRequest);

			HttpStatus code;
			Identity identity;
			AuthenticationResponse authenticationResponse = null;
			if (identityOpt.isPresent()) {
				identity = identityOpt.get();
				String authPass = authenticationRequest.getUser().getAuthenticationString();

				if (identity.getPassword().equals(authPass)) { // hashPass(authPass)
					code = HttpStatus.valueOf(200);
					authenticationResponse = genAuthResponse(key, authenticationRequest, identity, code);
				} else {
					code = HttpStatus.valueOf(405);
				}

			} else {
				code = HttpStatus.valueOf(405);
			}

			addResponseMap(key, authenticationResponse, code, reqExp);

			identitiyRequiestCash.remove(key);

		} else {
			log.debug("Key not present.");
		}

	}

	private String generateRelyingParty(String nid) {
		String relyingPartyRequestID = null;
		String timedNid = nid + String.valueOf(System.currentTimeMillis());
		byte[] bytes = null;
		try {
			bytes = timedNid.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("UTF-8 encode", e.getLocalizedMessage());
		}
		UUID uuid = UUID.nameUUIDFromBytes(bytes);
		relyingPartyRequestID = uuid.toString();
		return relyingPartyRequestID;
	}

	/*
	 * Save in inmemory map
	 */
	/**
	 * Put RAW identityInquiry/AuthenticationRequest
	 *
	 * @param relyingPartyRequestID
	 * @param authorizeKey
	 * @param authenticationRequest
	 * @param code
	 */
	private void addRequestMap(String relyingPartyRequestID, AuthenticationRequest authenticationRequest,
			HttpStatus code, long expPeriod) {
//		
//		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
//		request.getParameterNames()

//		ResponseStatus status = new ResponseStatus();
//		status.setFailure(false);
//		status.setStatusCode(String.valueOf(code.value()));
//
//		String httpEnum = "HTTP_" + code.value();
//		status.setStatusMessage(Enum.valueOf(IdentityInquiryCodes.class, httpEnum).getMsg());
//		// FIXME status.setSubStatusCode("???");

		UserStatusDataIn userStatusDataIn = new UserStatusDataIn();

		userStatusDataIn.setAuthenticationRequest(authenticationRequest);
//		userStatusDataIn.setStatus(status);

		identitiyRequiestCash.add(relyingPartyRequestID, userStatusDataIn, expPeriod, Cache.ExpiredType.EXPIRED_PERIOD);
	}
	
	private void addRequestMap(String relyingPartyRequestID, AuthenticationRequest authenticationRequest,
			HttpStatus code, OffsetDateTime expOffsetDateTime) {

//		ResponseStatus status = new ResponseStatus();
//		status.setFailure(false);
//		status.setStatusCode(String.valueOf(code.value()));
//
//		String httpEnum = "HTTP_" + code.value();
//		status.setStatusMessage(Enum.valueOf(IdentityInquiryCodes.class, httpEnum).getMsg());
//		// FIXME status.setSubStatusCode("???");

		UserStatusDataIn userStatusDataIn = new UserStatusDataIn();

		userStatusDataIn.setAuthenticationRequest(authenticationRequest);
//		userStatusDataIn.setStatus(status);

		identitiyRequiestCash.add(relyingPartyRequestID, userStatusDataIn, expOffsetDateTime);
	}
	
	

	/*
	 * Save in outMemory map
	 */
	private void addResponseMap(String relyingPartyRequestID, AuthenticationResponse authenticationResponse,
			HttpStatus code, long expTime) {

//		ResponseStatus status = new ResponseStatus();
//		status.setFailure(false);
//		status.setStatusCode(String.valueOf(code.value()));
//
//		String httpEnum = "HTTP_" + code.value();
//		status.setStatusMessage(Enum.valueOf(IdentityInquiryCodes.class, httpEnum).getMsg());
//		status.setSubStatusCode("???");

		UserStatusDataOut userStatusData = new UserStatusDataOut();
		userStatusData.setAuthenticationResponse(authenticationResponse);
//		userStatusData.setStatus(status);

		identitiyResponseCash.add(relyingPartyRequestID, userStatusData, expTime, Cache.ExpiredType.EXPIRED_TIME);
	}
	
	private AuthenticationResponse genAuthResponse(String relyingPartyRequestID, AuthenticationRequest body,
			Identity identity, HttpStatus code) {
		
		//TODO Read code from SecurityContextHolder ? attribute

		AuthenticationResponse authenticationResponse = new AuthenticationResponse(); 
		// TODO authenticationResponse.setId();

		UserAuthData userAuthData = body.getUser();

		IdentityData identityData = new IdentityData();
		/*
		 * Update UserAuthData w/ Identity DB profile data
		 */
		if (identity != null) {
			if ("egn".equals(defaultIdentity)) {
				userAuthData.setIdentityString(identity.getNid());
			} else if ("email".equals(defaultIdentity)) {
				userAuthData.setIdentityString(identity.getEmail());
			} else if ("phone".equals(defaultIdentity)) {
				userAuthData.setIdentityString(identity.getPhone());
			} else {
				log.error("Missing IDENTITY TYPE @genAuthResponse");
			}

			UserData userData = new UserData();
			userData.setEmail(identity.getEmail());
			userData.setIdentificationNumber(identity.getNid());
			userData.setName(identity.getNames());
			userData.setPhone(identity.getPhone());

			identityData.setIdentified(userData);

			AttributeMap attributeMap = new AttributeMap();
			attributeMap.put(AttributeMapKeys.BIRTH_FIRST_NAME.name(), identity.getNames());

			identityData.setAdditionalIdentityAttributes(attributeMap);
		}

		authenticationResponse.setSubject(identityData);
		// authenticationResponse.setAttributes(body.getAdditionalAttributes());
		authenticationResponse.setClientIpAddress("");
		// authenticationResponse.setInResponseToId(body.getVendorId());
		// authenticationResponse.setIssuer(body.getRequestProvider());
		authenticationResponse.setLevelOfAssurance(body.getLevelOfAssurance());
		authenticationResponse.setRelayState(body.getRelayState());
		
		return authenticationResponse;
	}

	@Autowired
	RequestContextHolder rch;

	private AuthenticationCallbackResult genAuthCallBackResponse(String relyingPartyRequestID, AuthenticationRequest body,
			Identity identity, HttpStatus code) {
		
		//TODO Read code from SecurityContextHolder ? attribute
		
		AuthenticationCallbackResult authenticationCallbackResult = new AuthenticationCallbackResult();
		
		authenticationCallbackResult.setRelyingPartyRequestID(relyingPartyRequestID);
		
		if (code.value() == 200) {			
			authenticationCallbackResult.setSuccess(true);
		} else {
			authenticationCallbackResult.setSuccess(false);
			authenticationCallbackResult.setErrorMessage(code.getReasonPhrase());
		}
		
		AuthenticationResponse authenticationResponse =  genAuthResponse(relyingPartyRequestID, body, identity, code);
		authenticationCallbackResult.setAuthResult(authenticationResponse);
		
		return authenticationCallbackResult;
		
	}

	@SuppressWarnings("unused")
	private enum AuthReqCodes {

		HTTP_200("Successful operation"), HTTP_400("Invalid status value"), HTTP_401(
				"API key is missing or invalid"), HTTP_405(
						"Invalid request"), HTTP_438("Issuer not found"), HTTP_451("Responce Not Ready");

		String msg;

		AuthReqCodes(String msg) {
			this.msg = msg;
		}

		String getMsg() {
			return this.msg;
		}
	}

	private enum IdentityInquiryCodes {

		HTTP_200("Request accepted successfuly"), HTTP_400("Invalid data supplied"), HTTP_401(
				"API key is missing or invalid"), HTTP_405("Invalid request"), HTTP_454("Incorrect coverage");

		String msg;

		IdentityInquiryCodes(String msg) {
			this.msg = msg;
		}

		String getMsg() {
			return this.msg;
		}
	}

	@SuppressWarnings("unused")
	private enum IdentityCalbackCodes {

		HTTP_400("Invalid data supplied"), HTTP_401("API key is missing or invalid"), HTTP_405(
				"Invalid request"), HTTP_438("Issuer not found"), HTTP_454("Incorrect coverage");

		String msg;

		IdentityCalbackCodes(String msg) {
			this.msg = msg;
		}

		String getMsg() {
			return this.msg;
		}
	}

	private String hashPass(String pass) {
		// FIXME SALT configuration???
		String salt = "";
		String toEncrypt = pass + salt;
		byte[] encHash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] dataBytes = toEncrypt.getBytes(Charsets.UTF_8);
			encHash = md.digest(dataBytes);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getLocalizedMessage());
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < encHash.length; i++) {
			sb.append(Integer.toString((encHash[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String getAutKey(AuthenticationRequest body) {
		// Convert model to JsonString
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(body);
		} catch (JsonProcessingException e) {
			log.error(e.getLocalizedMessage());
		}
		// Encode
		//TODO get "vendor key" from Configuration
		return AutorizeKey.encodeHmacSHA256(jsonString, "XXX");
	}

	@SuppressWarnings("unused")
	private void logCacheStatus(String methodDestination, String beforeAfter) {
		log.info("# " + beforeAfter + " - " + methodDestination + " - " + "identitiyREQuiestCash["
				+ identitiyRequiestCash.sizeAll() + "]");
		log.info("# " + beforeAfter + " - " + methodDestination + " - " + "identitiyRESPonseCash["
				+ identitiyResponseCash.sizeAll() + "]");
	}
	
	public static void main(String[] args) {

		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		Enumeration<String> pars = request.getParameterNames();
		for (String string: args) {
			log.info("Parameter: {}", string);
		}
	}
	
}
