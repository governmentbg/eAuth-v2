package bg.bulsi.egov.idp.client.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import bg.bulsi.egov.eauth.eid.client.ApiCustomClient.ResponceCodes;
import bg.bulsi.egov.eauth.eid.client.exception.ApiClientException;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.ProcessingData;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.idp.client.ProviderIdSuffix;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.ProviderAuthAttribute;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.exception.UiBackendException;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractApiClient implements IEidProviderClient {

	private static final long serialVersionUID = 5117998434802685550L;

	@Autowired
	protected EidProvidersConfiguration eidProvidersConfiguration;

	protected EidProviderConfig providerConfig;

	protected AuthenticationRequest authenticationRequest;

	/*
	 * "/inquiry"
	 */
	@Value("${egov.eauth.sys.eid.client.inq.url}")
	protected String inquiryUriPathSufix;

	/*
	 * "/signed/result/{relyingPartyRequestID}"
	 */
	@Value("${egov.eauth.sys.eid.client.auth.url}")
	protected String authUriPathSufix;

	/*
	 * "/signed/addAtt/{relyingPartyRequestID}"
	 */
	@Value("${egov.eauth.sys.eid.client.add.att}")
	protected String addAttUriPathSufix;

	@Override
	public EidProviderConfig getIdentityProviderConfig(String providerId) {
		providerConfig = eidProvidersConfiguration.getProviders().get(providerId);
		return providerConfig;
	}

	protected void configAuthRequest(EidProviderConfig eidProviderConfig, List<AssertionAttributeType> authList,
			AuthenticationMap auth) {
		AuthenticationRequest authReq = new AuthenticationRequest();

		authReq.setRelayState(null);

		ProcessingData processing = new ProcessingData();
		processing.setPtype(eidProviderConfig.getEidProcesss());
		LocalDateTime nowDate = java.time.LocalDateTime.now();
		int delta = eidProviderConfig.getExpirationPeriod();
		LocalDateTime date = nowDate.plusSeconds(delta);
		log.debug("Request expired DATES to '{}' from request '{}', expiration delta '{}sec' ", date, nowDate, delta);

		OffsetDateTime offsetDateTime = getLocalDateTime(date);
		processing.setResponceTimeout(OffsetDateTime.from(offsetDateTime));
		processing.setCallbackUrl(eidProviderConfig.getEidCallbackUrl());
		authReq.setProcessing(processing);

		authReq.setLevelOfAssurance(eidProviderConfig.getLoa());

		UserAuthData user = new UserAuthData();
		log.info("Authentication attributes: {}", eidProviderConfig.getAttributes());
		if (eidProviderConfig.getAttributes() != null && eidProviderConfig.getAttributes().size() > 0) {
			for (Entry<String, ProviderAuthAttribute> attSet : eidProviderConfig.getAttributes().entrySet()) {
				switch (attSet.getValue().getEId()) {
				case IDENTITY:
					user.setIdentityString(prepareIdentityString(attSet.getValue(), auth));
					break;
				case PASSWORD:
					user.setAuthenticationString(auth.get(attSet.getValue().getId()));
					break;
				default:
					break;
				}
			}
		}

		authReq.setUser(user);
		authReq.setRequestedAddAuthAttributes(authList);

		// must not be null!
		authReq.setRequestSystem("");
		authReq.setRequestedResource("");

		this.authenticationRequest = authReq;

	}

	private String prepareIdentityString(ProviderAuthAttribute attDef, AuthenticationMap auth) {
		StringBuffer identityBulder = new StringBuffer();
		identityBulder.append(attDef.getType().getPrefix());
		identityBulder.append(auth.get(attDef.getId()));
		return identityBulder.toString();

	}

	public String getProviderAttrKeyById(String providerId, ProviderIdSuffix keyType) {
		return providerId + "_" + keyType.name();
	}

	protected HttpHeaders setApiKeyHeaderParams(EidProviderConfig identityProviderConfig) {
		HttpHeaders initHeader = new HttpHeaders();
		initHeader.add(identityProviderConfig.getProviderApiKey(), identityProviderConfig.getProviderId());
		return initHeader;
	}

	public OffsetDateTime getLocalDateTime(java.time.LocalDateTime date) {
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffSet = zone.getRules().getOffset(date);
		return date.atOffset(zoneOffSet);
	}

	protected LoginResponse mapAuthentication2Login(AuthenticationResponse authResponse) throws UiBackendException {
		LoginResponse loginResponse = new LoginResponse();
		/*
		 * Mapping AssertionAttributes to LoginResponse
		 */
		// if(authResponse!=null &&
		// StringUtils.isNotBlank(authResponse.getInResponseToId()) &&
		// authResponse.getLevelOfAssurance()!=null) {
		if (authResponse != null) {
			loginResponse.setInResponseTid(authResponse.getInResponseToId());
			loginResponse.setRelayState(authResponse.getRelayState());
			loginResponse.setLoa(LevelOfAssurance.fromValue(authResponse.getLevelOfAssurance().name()));
			loginResponse.setProviderId(authResponse.getId());
			// FIXME sync LoginResponse object missed props
			// authResponse.getId()
			// authResponse.getClientIpAddress()
			// authResponse.getIssuer()

			List<IdentityAttributes> identityAttributes = authResponse.getSubjectAssertions().stream().map(att -> {
				IdentityAttributes identityItem = new IdentityAttributes();
				identityItem.setUrn(att.getAttribute().toString());
				identityItem.setValue(att.getValue());
				return identityItem;
			}).collect(Collectors.toCollection(ArrayList::new));

			loginResponse.setAttributes(identityAttributes);

			return loginResponse;
		} else {
			throw new UiBackendException("Unsuccessfull authentication!", HttpStatus.UNAUTHORIZED.value());
		}

	}

	protected long getDelayInSeconds(int timeout, OffsetDateTime validity) {
		LocalDateTime date = LocalDateTime.now();
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffSet = zone.getRules().getOffset(date);
		OffsetDateTime now = date.atOffset(zoneOffSet);
		OffsetDateTime timeoutValidity = now.plusSeconds(timeout);
		if(timeoutValidity.isBefore(validity)) {
			log.debug("Wait fixed timeout of '{}s'!",timeout);
			return timeout;
		}else{
			Duration duration = Duration.between(now, validity);
			log.debug("Wait validity period of '{}s'!",duration.getSeconds());
			return duration.getSeconds();
		}
	}
	
	protected boolean is5xxServerError(Throwable throwable) {
	    return throwable instanceof WebClientResponseException &&
	            ((WebClientResponseException) throwable).getStatusCode().is5xxServerError();
	}
	protected boolean is203NotReadyError(Throwable throwable) {
	    return throwable instanceof ApiClientException &&
	            ((ApiClientException) throwable).getResponceCode() == 203;
	}
}
