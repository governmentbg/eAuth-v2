package bg.bulsi.egov.eauth.eid.provider.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
//import bg.bulsi.egov.eauth.audit.model.DataKeys;
//import bg.bulsi.egov.eauth.audit.model.EventTypes;
//import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.eid.ExIdentApiDelegate;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeValue;
import bg.bulsi.egov.eauth.eid.dto.AuthProcessingType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.eid.dto.ProcessingData;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.eauth.eid.provider.cash.Cache;
import bg.bulsi.egov.eauth.eid.provider.cash.InMemoryCache;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import bg.bulsi.egov.eauth.eid.provider.model.UserStatusDataIn;
import bg.bulsi.egov.eauth.eid.provider.model.UserStatusDataOut;
import bg.bulsi.egov.eauth.eid.provider.model.repository.IdentityRepository;
import bg.bulsi.egov.eauth.eid.provider.service.exception.EidProviderException;
import bg.bulsi.egov.eauth.eid.provider.utils.AutorizeKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EidService implements ExIdentApiDelegate {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Value("${eid.provider.identity.type}")
	private String defaultIdentity;
	
	@Value("${eid.provider.vendor.id}")
	private String vendorId;
	
	@Value("${eid.provider.vendor.key}")
	private String vendorKey;

	/*
	 * String relyingPartyRequestID UserStatusData RAWin
	 */
	private static Cache<UserStatusDataIn> identitiyRequiestCash = new InMemoryCache<>();
	/*
	 * String relyingPartyRequestID UserStatusData out
	 */
	private static Cache<UserStatusDataOut> identitiyResponseCash = new InMemoryCache<>();

	@Autowired
	private IdentityRepository identityRepository;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Use temporary String authorizeKey =
	 * "32edfe308b6a5c627fdef0cab411a79ab1a0a73b19fae24d7663cf33dca1863a"; for
	 * authentication
	 * 
        @ApiResponse(code = 200, message = "Request accepted successfuly", response = InquiryResult.class),
        @ApiResponse(code = 400, message = "Bad Request, user can change it and resubmit new correct request.", response = CommonAuthException.class),
        @ApiResponse(code = 401, message = "API key is missing or invalid.", response = CommonAuthException.class),
        @ApiResponse(code = 405, message = "Invalid request or not allowed method.", response = CommonAuthException.class),
        @ApiResponse(code = 404, message = "The specified resource was not found.", response = CommonAuthException.class),
        @ApiResponse(code = 500, message = "The server encountered an unexpected exception.", response = ProcessingException.class) })
   	 */
	@Override
	public ResponseEntity<InquiryResult> identityInquiry(AuthenticationRequest body) throws EidProviderException{

//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		HttpServletRequest request = attr.getRequest();
//		apiKey = (String) request.getAttribute("api_key");
//		vendorId = (String) request.getAttribute("vendor_id");
//		vendorKey = (String) request.getAttribute("vendor_key");
		
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

			throw new EidProviderException("The specified resource was not found", 204);
		}

		String relyingPartyRequestID = generateRelyingParty(id);

		OffsetDateTime validityDateTime = processingData.getResponceTimeout();
		
		if (AuthProcessingType.POLLING.equals(processingData.getPtype())) {
		
			/*
			 * Add in IN HashMap
			 */
			//addRequestMap(relyingPartyRequestID, body, HttpStatus.valueOf(200), validityDateTime);
			addRequestMap(relyingPartyRequestID, body, HttpStatus.valueOf(200), processingData.getResponceTimeout().toInstant().toEpochMilli());
		} else if (AuthProcessingType.CALLBACK.equals(processingData.getPtype())) {
			
			Map<String,Object> uriVariables = new HashMap<>();
			//uriVariables.put("api_key", apiKey);
			uriVariables.put("vendor_id", vendorId);
			uriVariables.put("vendor_key", vendorKey);
			
			HttpStatus code = null;
			AuthenticationCallbackResult authenticationCallbackResult;
			
			
			// TODO CALLBACK URL
			UriComponentsBuilder builder = UriComponentsBuilder
					.fromUriString(processingData.getCallbackUrl())
					.encode(StandardCharsets.UTF_8)
//					.queryParam("api_key", apiKey)
//					.queryParam("vendor_id", vendorId)
//					.queryParam("vendor_key", vendorKey)
					;
			log.info("uri: [{}]", builder.toUriString());

			long timeoutTime = processingData.getResponceTimeout().toInstant().toEpochMilli();
			long currentTime = OffsetDateTime.now().toInstant().toEpochMilli();
			log.info("TIME must like {} > {}", timeoutTime, currentTime);
			
			if (isResponceTimeoutValid(processingData)) {
				
				code = HttpStatus.OK; //HttpStatus.ACCEPTED;
				authenticationCallbackResult = genAuthCallBackResponse(relyingPartyRequestID, body, identity, code);
				restTemplate.postForEntity(builder.toUriString(), authenticationCallbackResult,  AuthenticationCallbackResult.class, uriVariables);
			
			} else {
				//FIXME only err response w/o body
				code = HttpStatus.GATEWAY_TIMEOUT;
				authenticationCallbackResult = genAuthCallBackResponse(relyingPartyRequestID, body, identity, code);
				restTemplate.postForEntity(builder.toUriString(), authenticationCallbackResult,  AuthenticationCallbackResult.class, uriVariables);
				
			}
			
		}
		
		InquiryResult inq = new InquiryResult();
		inq.setRelyingPartyRequestID(relyingPartyRequestID);
		inq.setValidity(validityDateTime);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		//TODO get from configuration 3rd.idp#provider-api-key
		//headers.set("api_key", apiKey);
		headers.set("vendor_id", vendorId);
		headers.set("vendor_key", vendorKey);

		/*
		 * @Disabled publishing AuditEvent
		 */
		@SuppressWarnings("unused")
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.TEST_EID_AUTHN_SUCCESS)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		// applicationEventPublisher.publishEvent(auditApplicationEvent);

		return ResponseEntity.status(HttpStatus.valueOf(ResponceCodes.HTTP_200.code)).headers(headers).body(inq);
		// return new ResponseEntity<>(inq, HttpStatus.OK);
	}
	
	private boolean isResponceTimeoutValid(ProcessingData processingData) {
		return processingData.getResponceTimeout().toInstant().toEpochMilli() > OffsetDateTime.now().toInstant().toEpochMilli();
	}

	private Optional<Identity> findIdentity(AuthenticationRequest body) {
		UserAuthData userAuthData = body.getUser();
		String id = userAuthData.getIdentityString();
		log.info("identity str: [{}]", id);
		String auth = userAuthData.getAuthenticationString();
		log.info("auth str: [{}]", auth);

		Optional<Identity> identityOpt = Optional.empty();

		// FIXME Change identity type with some Enum
		if ("egn".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByNid(id);
		} else if ("email".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByEmail(id);
		} else if ("phone".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByPhone(id);
		} else if ("credentials".equals(defaultIdentity)) {
			identityOpt = identityRepository.findByUsername(id);
//			if (identityOpt.isPresent() && !passwordEncoder.matches(auth, identityOpt.get().getPassword())) {
			if (identityOpt.isPresent() && !auth.equals(identityOpt.get().getPassword())) {
				log.debug("Password for user '{}' didn't match!", id);
				identityOpt = Optional.empty();
			}
		} else {
			log.error("Uncknown IDENTITY TYPE ");
			throw new EidProviderException("Missing identity type", 400);
		}
		
		if(!identityOpt.isPresent()){
			log.error("Missing registered IDENTITY");
			throw new EidProviderException("Missing identity", 404);
		}

		return identityOpt;
	}

	/**
	 * 
        @ApiResponse(code = 200, message = "Request successfuly processed", response = AuthenticationResponse.class),
        @ApiResponse(code = 203, message = "Request is accepted, but still in processing.", response = ProcessingException.class),
        @ApiResponse(code = 400, message = "Bad Request, user can change it and resubmit new correct request.", response = CommonAuthException.class),
        @ApiResponse(code = 401, message = "API key is missing or invalid.", response = CommonAuthException.class),
        @ApiResponse(code = 404, message = "The specified resource was not found.", response = CommonAuthException.class),
        @ApiResponse(code = 409, message = "There is a conflict within this request. The user might be able to resolve the conflict and resubmit the request.", response = ProcessingException.class),
        @ApiResponse(code = 501, message = "The specified resource is not yet implemented.", response = CommonAuthException.class) })
    */
	@Override
	public ResponseEntity<AuthenticationResponse> getAuthentication(String relyingPartyRequestID) {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		//HttpServletRequest request = attr.getRequest();
		long inputCacheSize = identitiyRequiestCash.size();
		long responseCacheSize = identitiyResponseCash.size();
		log.debug("get Authentication for '{}' from input cache with size {} and response cache with size {}",relyingPartyRequestID,inputCacheSize,responseCacheSize);
		
		if (inputCacheSize==0 && responseCacheSize==0) {
			log.debug("No active authentication inquries!");
			throw new EidProviderException(ResponceCodes.HTTP_409.getMsg(), ResponceCodes.HTTP_409.getCode());
		}
		UserStatusDataOut userStatusDataOut = identitiyResponseCash.get(relyingPartyRequestID);
		//FIXME check if must remove before expired or before return Entity<>(OK)
//		identitiyResponseCash.remove(relyingPartyRequestID);

		if ( userStatusDataOut != null && userStatusDataOut.getAuthenticationResponse() != null) {
			AuthenticationResponse authenticationResponse = userStatusDataOut.getAuthenticationResponse();
			
			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
						.type(EventTypes.TEST_EID_AUTHN_SUCCESS)
						.data(DataKeys.SOURCE, this.getClass().getName())
						.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);
			
			//identitiyResponseCash.remove(relyingPartyRequestID);
			return new ResponseEntity<>(authenticationResponse, HttpStatus.valueOf(ResponceCodes.HTTP_200.code));
		} else {
			

			/*
			 * AuditEvent
			 */
			AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
						.type(EventTypes.TEST_EID_AUTHN_FAILED)
						.data(DataKeys.SOURCE, this.getClass().getName())
						.build();
			applicationEventPublisher.publishEvent(auditApplicationEvent);

			throw new EidProviderException(ResponceCodes.HTTP_203.getMsg(), ResponceCodes.HTTP_203.getCode());
		}

	}
	
	

	/**
	 * Read/Remove from RequestMap and 
	 * Write to ResponseMap
	 *
	 * Sheduled through:
	 * SchedulerTask &&
	 * AsyncTaskShed
	 *
	 * @return
	 */
	public void poolInMemoryToIdentityInquiry() {
		
		if (identitiyRequiestCash.size()>0) {
			
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
						
						code = HttpStatus.valueOf(ResponceCodes.HTTP_200.code);
						authenticationResponse = genAuthResponse(key, authenticationRequest, identity, code);
					} else {
						code = HttpStatus.valueOf(ResponceCodes.HTTP_405.code); //INVALID REQUEST (bad password)
					}
	
				} else {
					code = HttpStatus.valueOf(ResponceCodes.HTTP_404.code); //Not Found
				}
				log.debug("Add authentication response for '{}' with status {}",key,code.name());
				addResponseMap(key, authenticationResponse, code, reqExp);
				log.debug("Remove inqury cache for '{}'",key);
				identitiyRequiestCash.remove(key);
	
			} else {
				log.debug("Key not present.");
			}
			
		}

	}

	public static String generateRelyingParty(String nid) {
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

		identitiyRequiestCash.add(relyingPartyRequestID, userStatusDataIn, expPeriod, Cache.ExpiredType.EXPIRED_TIME);
	}
	@Deprecated
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

		List<AssertionAttributeValue> assertionAttributeValues = new ArrayList<>();
		
		//Mandatory PERSONIDENTIFIER and PERSONNAME
		AssertionAttributeValue assertionIdentityAttributeValue = new AssertionAttributeValue();
		assertionIdentityAttributeValue.setAttribute(AssertionAttributeType.PERSONIDENTIFIER);
		//XXX fix in the DB structure or getter
		assertionIdentityAttributeValue.setValue("PNOBG-"+identity.getNid());	
		assertionAttributeValues.add(assertionIdentityAttributeValue);
		
		AssertionAttributeValue assertionNameAttributeValue = new AssertionAttributeValue();
		assertionNameAttributeValue.setAttribute(AssertionAttributeType.PERSONNAME);
		assertionNameAttributeValue.setValue(identity.getNames());
		assertionAttributeValues.add(assertionNameAttributeValue);
		
		//Map listValue@Type with Identity
		@Valid List<AssertionAttributeType> atts = body.getRequestedAddAuthAttributes();
		Iterator<AssertionAttributeType> iterator = atts.iterator();
		while (iterator.hasNext()) {
			AssertionAttributeValue assertionAttributeValue = new AssertionAttributeValue();
			AssertionAttributeType attType = iterator.next();
			String value = null;
			switch (attType) {
			//Optional
				case EMAIL:
					value = identity.getEmail();
					break;
				case PHONE:
					value = identity.getPhone();
					break;
				case LATINNAME:
					value = identity.getNames();
					break;
				case BIRTHNAME:
					value = identity.getNames();
					break;
				case GENDER:
//					value = identity.get???;
					break;
				case DATEOFBIRTH:
//					value = identity.get???;
					break;
				case PLACEOFBIRTH:
//					value = identity.get???;
					break;
				case X509:
//					value = identity.get???;
					break;
				case CANONICALRESIDENCEADDRESS:
//					value = identity.get???;
					break;
				case PERSONIDENTIFIER:
				case PERSONNAME:
				default:
					break;
			}

			assertionAttributeValue.setAttribute(attType);
			assertionAttributeValue.setValue(value);
			assertionAttributeValues.add(assertionAttributeValue);
		}

		authenticationResponse.setSubjectAssertions(assertionAttributeValues);
		authenticationResponse.setId(relyingPartyRequestID);
		authenticationResponse.setClientIpAddress("");
//		authenticationResponse.setInResponseToId(body.get...);
//		authenticationResponse.setIssuer(body.get...);
		authenticationResponse.setLevelOfAssurance(body.getLevelOfAssurance());
		authenticationResponse.setRelayState(body.getRelayState());
		
		return authenticationResponse;
	}

	private AuthenticationCallbackResult genAuthCallBackResponse(String relyingPartyRequestID, AuthenticationRequest body,
			Identity identity, HttpStatus code) {
		
		//TODO Read code from SecurityContextHolder ? attribute
		
		AuthenticationCallbackResult authenticationCallbackResult = new AuthenticationCallbackResult();
		
		authenticationCallbackResult.setRelyingPartyRequestID(relyingPartyRequestID);
		
		if (code.value() == 200 || code.value() == 203) {			
			authenticationCallbackResult.setSuccess(true);
			authenticationCallbackResult.setErrorMessage(code.getReasonPhrase());
		} else {
			authenticationCallbackResult.setSuccess(false);
			authenticationCallbackResult.setErrorMessage(code.getReasonPhrase());
		}
		
		AuthenticationResponse authenticationResponse =  genAuthResponse(relyingPartyRequestID, body, identity, code);
		authenticationCallbackResult.setAuthResult(authenticationResponse);
		
		return authenticationCallbackResult;
		
	}


	@SuppressWarnings("unused")
	private enum ResponceCodes {

		HTTP_200(200,"Request successfuly processed"), 
		HTTP_203(203,"Request is accepted but still in processing"), 
		HTTP_400(400,"Bad Request, user can change it and resubmit new correct request"), 
		HTTP_401(401,"API key is missing or invalid"), 
		HTTP_404(404,"The specified resource was not found"), 
		HTTP_405(405,"Invalid request or not allowed method"), 
		HTTP_409(409,"The user might be able to resolve the conflict and resubmit the request"), 
		HTTP_500(500,"The server encountered an unexpected exception"),
		HTTP_501(501,"Not Implemented");

		@Getter
		int code;
		@Getter
		String msg;

		ResponceCodes(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

	}

	//Uses by default Openbsd BCrypt

	@SuppressWarnings("unused")
	private String hashPass(String pass) {
		return 	passwordEncoder.encode(pass);
	}

	/**
	 * Use "vendorKey" from Configuration
	 * as secret
	 * 
	 * @param body
	 * @return
	 */
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
		return AutorizeKey.encodeHmacSHA256(jsonString, vendorKey);
	}
	
	public boolean isTransferred(String realingPartyId) {
		return identitiyResponseCash.contains(realingPartyId);
	}

}
