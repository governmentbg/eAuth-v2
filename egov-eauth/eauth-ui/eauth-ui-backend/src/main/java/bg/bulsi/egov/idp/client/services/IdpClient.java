package bg.bulsi.egov.idp.client.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import bg.bulsi.egov.eauth.eid.dto.AttributeMap;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig.ProviderAuthAttribute;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig.ProviderIdSuffix;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class IdpClient implements IEidProviderClient {

	@Autowired
	EidProvidersConfiguration eidProvidersConfiguration;

	@Autowired
	private RestTemplate restTemplate;

	private AuthenticationRequest authenticationRequest;

	// test-provider-idp.eauth.egov.bg:8230

	@Value("${egov.eauth.sys.eid.client.inq.url}")
	private String INQUIRY_URI;

	@Value("${egov.eauth.sys.eid.client.auth.url}")
	private String AUTH_URI;

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public EidProviderConfig getIdentityProviderConfig(String providerId) {

		EidProviderConfig eidProviderConfig = eidProvidersConfiguration.getProviders().get(providerId);

		return eidProviderConfig;

	}


	@Override
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig, AuthenticationMap authMap) {

		this.authenticationRequest = configAuthRequest(identityProviderConfig, authMap);

		InquiryResult res = restTemplate.postForObject(INQUIRY_URI, this.authenticationRequest, InquiryResult.class);

		return res;

	}


	@SuppressWarnings("unused")
	@Override
	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID) {

		LoginResponse loginResponse = new LoginResponse();
		AuthenticationResponse authResponse = null;

		long expirationTime = identityProviderConfig.getExpirationPeriod();
		while (expirationTime > System.currentTimeMillis()) {

			/*
			 * CompletableFuture or Flux
			 */
			if (false) {
				CompletableFuture<AuthenticationResponse> authResponseCf = getAuthenticationResponse(identityProviderConfig, relyingPartyRequestID);
				try {
					authResponse = authResponseCf.get();
				} catch (InterruptedException e) {
					log.error(e.getLocalizedMessage());
				} catch (ExecutionException e) {
					log.error(e.getLocalizedMessage());
					
				}
			} else {
				Flux<AuthenticationResponse> authResponseFx = getAuthenticationResponseFlux(identityProviderConfig, relyingPartyRequestID);
				authResponse = authResponseFx.blockFirst();
			}

			if (authResponse != null) {
				break;
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getLocalizedMessage());
			}

		}

		// TODO mapping
		// loginResponse = EidMapping.map(authResponse, LoginResponse.class);
		loginResponse.setProviderId(authResponse.getId());
		loginResponse.setLoa(LevelOfAssurance.fromValue(authResponse.getLevelOfAssurance().name()));
		loginResponse.setRelayState(authResponse.getRelayState());
		loginResponse.setInResponseTid(authResponse.getInResponseToId());
		// FIXME correspondence fields
		// loginResponse.setAttributes(authResponse.getAttributes());

		return loginResponse;

	}


	private AuthenticationRequest configAuthRequest(EidProviderConfig eidProviderConfig, AuthenticationMap authMap) {

		AttributeMap attrMap = new AttributeMap();
		attrMap.putAll(authMap);

		AuthenticationRequest authenticationRequest = new AuthenticationRequest();
		authenticationRequest.setIdentificationAttributes(attrMap);

		authenticationRequest.setLevelOfAssurance(eidProviderConfig.getLoa());
		// authenticationRequest.setRelayState(idpParams.get);
		authenticationRequest.setRequestProvider(eidProviderConfig.getEndpoint());
		// authenticationRequest.setRequestedDestination(...);
		// authenticationRequest.setVendorId(...);

		authenticationRequest.getIdentificationAttributes().put("EXPIRATIONPERIOD", String.valueOf(eidProviderConfig.getExpirationPeriod()));

		// FIXME clean user.properties setValues from attributes
		UserAuthData user = new UserAuthData();
		authenticationRequest.setUser(user);

		if (eidProviderConfig.getAttributes() != null && eidProviderConfig.getAttributes().size() > 0) {
			for (Entry<String, ProviderAuthAttribute> attSet: eidProviderConfig.getAttributes().entrySet()) {
				switch (attSet.getValue().getEId()) {
					case IDENTITY:
						authenticationRequest.getUser().setIdentityString(attSet.getValue().getId());
						break;
					case PASSWORD:
						authenticationRequest.getUser().setAuthenticationString(attSet.getValue().getId());
						break;
					case ADDITIONAL:
						authenticationRequest.getIdentificationAttributes().put(attSet.getKey(), attSet.getValue().getId());
						break;
					default:
						break;
				}
			}
		}

		return authenticationRequest;

	}


	public String getProviderAttrKeyById(String providerId, ProviderIdSuffix keyType) {
		return providerId + "_" + keyType.name();
	}


	@Async
	public CompletableFuture<AuthenticationResponse> getAuthenticationResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID) {

		Map<String, String> params = new HashMap<>();
		params.put("relyingPartyRequestID", relyingPartyRequestID);

		return CompletableFuture.supplyAsync(() -> {
			AuthenticationResponse authResponseRE = restTemplate.getForObject(AUTH_URI, AuthenticationResponse.class, params);
			return authResponseRE;
		});

		// return CompletableFuture.completedFuture(authResponseRE);

	}


	public Flux<AuthenticationResponse> getAuthenticationResponseFlux(EidProviderConfig identityProviderConfig, String relyingPartyRequestID) {

		Flux<AuthenticationResponse> authFlux = WebClient.create()
				.get()
				.uri(uriBuilder -> uriBuilder
						.path(AUTH_URI)
						.build(relyingPartyRequestID))
				.retrieve()
				.bodyToFlux(AuthenticationResponse.class);

		log.info("http://localhost:8230/exIdent/signed/responce/".concat(relyingPartyRequestID));
		authFlux.subscribe(authRs -> log.info(authRs.toString()));

		return authFlux;

	}

}
