package bg.bulsi.egov.idp.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import bg.bulsi.egov.eauth.model.Preferred2FA;
import bg.bulsi.egov.eauth.model.User;
import bg.bulsi.egov.eauth.model.repository.UserRepository;
import bg.bulsi.egov.eauth.tfa.api.dto.AuthRequestDetails;
import bg.bulsi.egov.eauth.tfa.api.dto.CheckResult;
import bg.bulsi.egov.eauth.tfa.api.dto.OTPass;
import bg.bulsi.egov.eauth.tfa.api.dto.UserContactInfo;
import bg.bulsi.egov.hazelcast.enums.OTPMethods;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.hazelcast.util.HazelcastUtils;
import bg.bulsi.egov.idp.dto.CodeData;
import bg.bulsi.egov.idp.dto.EnabledOTPMethod;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.InlineResponse200;
import bg.bulsi.egov.idp.dto.OTPMethod;
import bg.bulsi.egov.idp.dto.OTPresponse;
import bg.bulsi.egov.idp.dto.SecretMetadata;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import bg.bulsi.egov.saml.model.AssertionAttributes;
import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import bg.bulsi.egov.security.utils.PersonalIdUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TfaService {

	private static final String MISSING_URL_PROPERTY = "missing";
	
	@Value("${egov.eauth.sys.tfa.email.auth.url:missing}")
	private String emailAuthUrl;

	@Value("${egov.eauth.sys.tfa.email.validate_url:missing}")
	private String emailValidateUrl;

	@Value("${egov.eauth.sys.tfa.sms.auth_url:missing}")
	private String smsAuthUrl;

	@Value("${egov.eauth.sys.tfa.sms.validate.url:missing}")
	private String smsValidateUrl;

	@Value("${egov.eauth.sys.tfa.totp.generate.url:missing}")
	private String totpGenerateUrl;

	@Value("${egov.eauth.sys.tfa.totp.validate.url:missing}")
	private String totpValidateUrl;

	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HazelcastUtils hazelcastUtils;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HazelcastService hazelcastService;
	
	@Autowired
    protected EauthProviderProperties properties;
	
	
	public OTPresponse send() {
		return send(null);
	}
	
	public OTPresponse send(OTPMethod otpMethod) {
		OTPresponse response = null;

		String nid = getNidFromIdentityAttribute();
		log.info("nid: [{}]", nid);

		Optional<User> userEntity = userRepository.findByPersonID(nid);
		if (userEntity.isPresent()) {
			User user = userEntity.get();

			// Send using predifined method in user profile
			Preferred2FA userPrefsMethod = user.getPreferred();
			OTPMethod method = otpMethod == null ? OTPMethod.valueOf(userPrefsMethod.name()) : otpMethod;
			String url = getSendCodeMethodUrl(method);
			if (MISSING_URL_PROPERTY.equals(url)) {
				// end point missing from db probperies
				return response;
			}

			response = new OTPresponse(); // initialize
			if (method == OTPMethod.TOTP) {
				ResponseEntity<SecretMetadata> secretResponse = null;
				Map<String, String> attributes = user.getAttributes();
				if (attributes == null || attributes.isEmpty() || !attributes.containsKey(User.TOTP_SECRET)) {
					secretResponse = generateTotpSecret();
				}
				String totpMessage = hazelcastService.get("egov.eauth.dyn.tfa.totp.msg");
				
				response.method(method)
						.message(totpMessage)
						.tid("totp-" + System.currentTimeMillis())
						.timestamp(null)
						.qrCode(secretResponse == null ? null : secretResponse.getBody());
			} else {
				UserContactInfo contacts = new UserContactInfo()
					.phone(user.getPhoneNumber())
					.email(user.getEmail());
				AuthRequestDetails authRequestDetails = new AuthRequestDetails()
						.contacts(contacts)
						.description(userPrefsMethod.toString());
						

				HttpEntity<AuthRequestDetails> request = new HttpEntity<>(authRequestDetails, getHeaders());
				OTPass tfaResponse = restTemplate.postForObject(url, request, OTPass.class);

				response.method(method)
						.message(generateSendMessage(method, user))
						.tid(tfaResponse.getTransaction())
						.timestamp(tfaResponse.getTimestamp());
			}

		}

		return response;
	}

	public InlineResponse200 validate(CodeData body) {
		InlineResponse200 response = null;

		OTPMethod method = body.getMethod();
		String url = getValidateCodeMethodUrl(method);
		if (MISSING_URL_PROPERTY.equals(url)) {
			// end point missing from db probperies
			return response;
		}

		StringBuilder builder = new StringBuilder(url);
		builder.append("?c=" + body.getCode());

		if (method == OTPMethod.TOTP) {
			String nid = getNidFromIdentityAttribute();
			// Replace special characters
			String encodedNid = nid;
			try {
				encodedNid = URLEncoder.encode(nid, StandardCharsets.UTF_8.name());
				log.debug("URL encode EGN {} to {}", nid, encodedNid);
			} catch (UnsupportedEncodingException e) {
				log.error(e.getMessage(), e);
			}
			builder.append("&u=" + encodedNid); // userId(EGN)
			String applicationID = "eauth"; // hardcoded

			CheckResult totpResponse = restTemplate.getForObject(builder.toString(), CheckResult.class, applicationID);
			response = new InlineResponse200().valid(totpResponse.isValid()).message(totpResponse.getStatus());
		} else {
			builder.append("&t=" + body.getTId()); // transactionId
			CheckResult validateResponse = restTemplate.getForObject(builder.toString(), CheckResult.class);
			response = new InlineResponse200().valid(validateResponse.isValid()).message(validateResponse.getStatus());
		}

		return response;
	}

	public List<EnabledOTPMethod> enabledUserOtpMethods() {
		List<EnabledOTPMethod> list = new ArrayList<>();
		
		String nid = getNidFromIdentityAttribute();
		log.info("nid: [{}]", nid);
		
		Optional<User> userEntity = userRepository.findByPersonID(nid);
		if (userEntity.isPresent()) {
			User user = userEntity.get();
			Preferred2FA defaultMethod = user.getPreferred();
			log.info("default method from profile: " + defaultMethod);
			
			List<OTPMethods> otpMethods = allEnabledOtpMethods();
			otpMethods.forEach(method -> {
				boolean isDefault = defaultMethod != null && defaultMethod.name().equals(method.name());
				list.add(new EnabledOTPMethod(OTPMethod.fromValue(method.name()), isDefault));
			});
		}

		return list;
	}
	
	public List<OTPMethods> allEnabledOtpMethods() {
		List<OTPMethods> allEnabledMethods = hazelcastUtils.getOTPMethodsEnabled();
		return allEnabledMethods;
	}

	public ResponseEntity<SecretMetadata> generateTotpSecret() {
		String nid = getNidFromIdentityAttribute();
		log.info("nid: [{}]", nid);
		HttpHeaders headers = getHeaders();
		headers.set("userID", nid); // add custom header

		HttpEntity<String> request = new HttpEntity<>(headers);
		String applicationId = "eauth"; // hardcoded

		ResponseEntity<SecretMetadata> response = restTemplate.exchange(totpGenerateUrl, HttpMethod.GET, request,
				SecretMetadata.class, applicationId);
		
		return response;
	}

	/*
	 * set common request headers
	 */
	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return headers;
	}

	private String getNidFromIdentityAttribute() {
		ExternalIdpUserAuthenticationToken token = (ExternalIdpUserAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		IdpPrincipal principal = (IdpPrincipal) token.getPrincipal();
		List<IdentityAttributes> identityAttributes = principal.getAttributes();
		IdentityAttributes identAttr = identityAttributes.stream()
				.filter(attr -> attr.getUrn().equals(AssertionAttributes.PERSON_IDENTIFIER.getEidUrn()))
				.findFirst().orElse(null);
		String nid = null;
		if (identAttr != null) {
			nid = parseAttributeAndEncryptNid(identAttr.getValue());
		}
		return nid;
	}
	
	private String parseAttributeAndEncryptNid(String value) {
		String parsed = value.substring(value.indexOf('-') + 1);
		log.info("parsed: [{}]", parsed);
		String encrypted = PersonalIdUtils.encrypt(parsed, properties.getIdSecret());
		log.info("encrypted: " + encrypted);
		return  encrypted;
	}

	private String getSendCodeMethodUrl(OTPMethod method) {
		log.info("method: [{}]", method.name());
		String url = null;

		switch (method) {
		case EMAIL:
			url = emailAuthUrl;
			break;
		case SMS:
			url = smsAuthUrl;
			break;
		case TOTP:
			url = totpGenerateUrl;
			break;

		default:
			log.error("Not supported method for TFA!");
			break;
		}
		return url;
	}

	private String getValidateCodeMethodUrl(OTPMethod method) {
		log.info("method: [{}]", method.name());
		String url = null;

		switch (method) {
		case EMAIL:
			url = emailValidateUrl;
			break;
		case SMS:
			url = smsValidateUrl;
			break;
		case TOTP:
			url = totpValidateUrl;
			break;
		default:
			log.error("Not supported method for TFA!");
			break;
		}
		return url;
	}

	private String generateSendMessage(OTPMethod method, User user) {
		// sms default
		String sendTo = user.getPhoneNumber();
		long smsExpirationTimeInSeconds = Long.valueOf(hazelcastService.get("egov.eauth.dyn.tfa.sms.otp.expiration"));
		long expirationTimeInMinutes = smsExpirationTimeInSeconds / 60;
		if (method.name().equals("EMAIL")) {
			sendTo = user.getEmail();
			long emailExpirationTimeInSeconds = Long.valueOf(hazelcastService.get("egov.eauth.dyn.tfa.email.otp.expiration"));
			expirationTimeInMinutes = emailExpirationTimeInSeconds / 60;
		}
		String sendMessage = hazelcastService.get("egov.eauth.dyn.tfa.send.msg");
		return MessageFormat.format(sendMessage, sendTo, expirationTimeInMinutes);
	}

	public boolean checkIfProfileTfaExists() {
		String ecryptedIdentifier = getNidFromIdentityAttribute();
		Optional<User> user = userRepository.findByPersonID(ecryptedIdentifier);
		return user.isPresent();
	}
}
