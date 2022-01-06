package bg.bulsi.egov.idp.client.services;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeValue;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
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
import bg.bulsi.egov.idp.services.IEidProviderClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Deprecated
public class IdpClient implements IEidProviderClient {

	@Autowired
	EidProvidersConfiguration eidProvidersConfiguration;

	@Autowired
	private RestTemplate restTemplate;

	private AuthenticationRequest authenticationRequest;

	// test-provider-idp.eauth.egov.bg:8230
	@Value("${egov.eauth.sys.eid.client.inq.url}")
	private String inquiryUri;

	@Value("${egov.eauth.sys.eid.client.auth.url}")
	private String authUri;

	@Override
	public EidProviderConfig getIdentityProviderConfig(String providerId) {

		return eidProvidersConfiguration.getProviders().get(providerId);

	}


	@Override
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig, List<AssertionAttributeType> authList, AuthenticationMap auth, String requestedResource, String requestSystem) {

		configAuthRequest(identityProviderConfig, authList);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		authenticationRequest.setRequestedResource(requestedResource);
		authenticationRequest.setRequestSystem(requestSystem);

		restTemplate.setDefaultUriVariables(headers);
		return restTemplate.postForObject(inquiryUri, this.authenticationRequest, InquiryResult.class, headers);
	}


	@SuppressWarnings("unused")
	@Override
	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID, OffsetDateTime inquiryValidity) {

		LoginResponse loginResponse = new LoginResponse();
		AuthenticationResponse authResponse = null;

		long expirationTime = identityProviderConfig.getExpirationPeriod();
		while (expirationTime > System.currentTimeMillis()) {

			/*
			 * CompletableFuture or Mono
			 */
			if (false) {
				CompletableFuture<AuthenticationResponse> authResponseCf = getAuthenticationResponse(identityProviderConfig, relyingPartyRequestID);
				try {
					authResponse = authResponseCf.get();
				} catch (InterruptedException e) {
					log.error(e.getLocalizedMessage());
					Thread.currentThread().interrupt();
				} catch (ExecutionException e) {
					log.error(e.getLocalizedMessage());
				}
			} else {
				Mono<AuthenticationResponse> authResponseFx = getAuthenticationResponseFlux(identityProviderConfig, relyingPartyRequestID);
				authResponse = authResponseFx.block();
			}

			if (authResponse != null) {
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getLocalizedMessage());
				Thread.currentThread().interrupt();
			}

		}

		/*
		 * Mapping AssertionAttributes to LoginResponse
		 */
		loginResponse.setInResponseTid(authResponse.getInResponseToId());
		loginResponse.setRelayState(authResponse.getRelayState());
		loginResponse.setLoa(LevelOfAssurance.fromValue(authResponse.getLevelOfAssurance().name()));
		loginResponse.setProviderId(authResponse.getId());
		//FIXME sync LoginResponse object missed props
		//		authResponse.getId()
		//		authResponse.getClientIpAddress()
		//		authResponse.getIssuer()

		List<IdentityAttributes> attributes = new LinkedList<>();
		//Mapping UserData fields
		while (authResponse.getSubjectAssertions().iterator().hasNext()) {
			AssertionAttributeValue attributeValue = authResponse.getSubjectAssertions().iterator().next();
			IdentityAttributes identityItem = new IdentityAttributes();
			identityItem.setUrn(attributeValue.getAttribute().toString()); // miss value Getter
			identityItem.setValue(attributeValue.getValue());
			attributes.add(identityItem);
		}
		loginResponse.setAttributes(attributes);

		return loginResponse;
	}
	

	private void configAuthRequest(EidProviderConfig eidProviderConfig, List<AssertionAttributeType> authList) {
		AuthenticationRequest authReq = new AuthenticationRequest();

		//authReq.setRequestedDestination(eidProviderConfig.getEndpoint()); //TODO delete missed
		
		//authReq.setRequestProvider(eidProviderConfig.getEndpoint()); //TODO delete missed
		
		authReq.setRelayState(null);

		ProcessingData processing = new ProcessingData();
		processing.setPtype(eidProviderConfig.getEidProcesss());
		java.time.LocalDateTime date = java.time.LocalDateTime.now().plusSeconds(eidProviderConfig.getExpirationPeriod());
		ZoneId zone = ZoneId.systemDefault();
		ZoneOffset zoneOffSet = zone.getRules().getOffset(date);
		OffsetDateTime offsetDateTime = date.atOffset(zoneOffSet);
		processing.setResponceTimeout(offsetDateTime);
		processing.setCallbackUrl(eidProviderConfig.getEidCallbackUrl());
		authReq.setProcessing(processing);
		
		authReq.setLevelOfAssurance(eidProviderConfig.getLoa());

		UserAuthData user = new UserAuthData();
		if (eidProviderConfig.getAttributes() != null && eidProviderConfig.getAttributes().size() > 0) {
			for (Entry<String, ProviderAuthAttribute> attSet: eidProviderConfig.getAttributes().entrySet()) {
				switch (attSet.getValue().getEId()) {
					case IDENTITY:
						user.setIdentityString(attSet.getValue().getId());
						break;
					case PASSWORD:
						user.setAuthenticationString(attSet.getValue().getId());
						break;
					default:
						break;
				}
			}
		}
		authReq.setUser(user);
		authReq.setRequestedAddAuthAttributes(authList);
		
		this.authenticationRequest = authReq;

	}


	public String getProviderAttrKeyById(String providerId, ProviderIdSuffix keyType) {
		return providerId + "_" + keyType.name();
	}


	@Async
	public CompletableFuture<AuthenticationResponse> getAuthenticationResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID) {

		Map<String, String> params = new HashMap<>();
		params.put("relyingPartyRequestID", relyingPartyRequestID);

		return CompletableFuture.supplyAsync(() -> 
			restTemplate.getForObject(authUri, AuthenticationResponse.class, params)
		);

	}


	public Mono<AuthenticationResponse> getAuthenticationResponseFlux(EidProviderConfig identityProviderConfig, String relyingPartyRequestID) {

		Mono<AuthenticationResponse> authFlux = WebClient.create()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(authUri)
						.build(relyingPartyRequestID))
				.retrieve()
				.bodyToMono(AuthenticationResponse.class);

		log.info("http://localhost:8230/exIdent/signed/responce/".concat(relyingPartyRequestID));
		authFlux.subscribe(authRs -> log.info(authRs.toString()));

		return authFlux;

	}

}
