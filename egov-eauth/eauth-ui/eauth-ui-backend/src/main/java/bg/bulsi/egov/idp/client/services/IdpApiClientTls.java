package bg.bulsi.egov.idp.client.services;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;

import bg.bulsi.egov.eauth.eid.client.ApiClient;
import bg.bulsi.egov.eauth.eid.client.ApiCustomClient;
import bg.bulsi.egov.eauth.eid.client.exception.ApiClientException;
import bg.bulsi.egov.eauth.eid.client.exception.EidProviderException;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.client.config.model.KeyStoreData;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.exception.UiBackendException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Component
@DependsOn({ "keyStoreIdp", "trustStoreIdp" })
public class IdpApiClientTls extends AbstractApiClient {

	private static final long serialVersionUID = 8069406248496075911L;
	//TODO get from configuration
	private static final int repeatNums = 6;

	@Autowired
	@Qualifier("keyStoreIdp")
	KeyStore keyStoreIdp;

	@Autowired
	@Qualifier("trustStoreIdp")
	KeyStore trustStoreIdp;

	@Value("${server.ssl.trust-store}")
	private String trustStorePath;

	@Value("${server.ssl.trust-store-password}")
	private String trustStorePass;

	@Value("${server.ssl.trust-store-type}")
	private String trustStoreType;


	@Override
	public InquiryResult makeAuthInquiry(
			EidProviderConfig identityProviderConfig, List<AssertionAttributeType> authList, AuthenticationMap auth, String requestedResource, String requestSystem) {
		configAuthRequest(identityProviderConfig, authList, auth);

		ApiClient apiClient = getApiClient(identityProviderConfig);

		/*
		 * invokeApi parameters
		 */
		HttpMethod method = HttpMethod.POST;
		Map<String, Object> pathParams = new HashMap<>();
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		AuthenticationRequest body = this.authenticationRequest;
		body.setRequestedResource(requestedResource);
		body.setRequestSystem(requestSystem);
		/*
		 * UserAuthData userAuthData = body.getUser();
		 * log.info("userAuthData: [{}]", userAuthData.toString());
		 * String identityString = userAuthData.getIdentityString();
		 * log.info("identityString: [{}]", identityString);
		 * String authenticationString = userAuthData.getAuthenticationString();
		 * log.info("authenticationString: [{}]", authenticationString);
		 */
		log.info("body: [{}]", body.toString());

		HttpHeaders headerParams = setApiKeyHeaderParams(identityProviderConfig);
		MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
		MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();
		List<MediaType> accept = new LinkedList<>();
		MediaType contentType = MediaType.APPLICATION_JSON;
		String[] authNames = new String[] {};
		ParameterizedTypeReference<InquiryResult> returnType = new ParameterizedTypeReference<InquiryResult>() {};

		Mono<?> resApi;
		Object result;
		InquiryResult inqRes = null;
		try {
			resApi = apiClient.invokeAPI(inquiryUriPathSufix, method, pathParams, queryParams, body,
					headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
			result = resApi.block();
		} catch (RestClientException  e) {
			throw new UiBackendException(e);
		} catch (IllegalStateException e) {
			throw new UiBackendException("Missing identity", e, 404);
		}

		if (result == null) {
			// throw new UiBackendException(ApiCustomClient.ResponceCodes.HTTP_203.getMsg(), ApiCustomClient.ResponceCodes.HTTP_203.getCode());
			return null;
		} else if (result instanceof InquiryResult) {
			inqRes = (InquiryResult) result;
		} else {
			inqRes = null;
			ApiClientException exRes = (ApiClientException) result;
			throw new UiBackendException(exRes.getLocalizedMessage(), exRes, exRes.getResponceCode());
		}

		if (inqRes != null && StringUtils.isNotBlank(inqRes.getRelyingPartyRequestID())) {
			return inqRes;
		} else {
			throw new UiBackendException("Can't access external authentication service!", HttpStatus.UNAUTHORIZED.value());
		}

	}


	/**
	 * @param identityProviderConfig
	 * @return
	 */
	private ApiClient getApiClient(EidProviderConfig identityProviderConfig) {
		KeyStoreData ksd = identityProviderConfig.getEidProviderConnection().getClientKeyStore();
		ApiClient apiClient;
		if (identityProviderConfig.getEidProviderConnection().isClientSslRequired()) {
			log.info("## Use certificate from Keystore with Type: '{}' and pass '{}' by alias '{}'", ksd.getType(), ksd.getPass(), ksd.getAlias());

			Key key = null;
			try {
				key = keyStoreIdp.getKey(ksd.getAlias(), ksd.getPass().toCharArray());
			} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
				log.error(e.getLocalizedMessage(), e);
			}
			log.info("## KEY : {}", key.getAlgorithm());
			apiClient = new ApiCustomClient(trustStoreIdp, trustStorePass, trustStoreType,
											keyStoreIdp, ksd.getPass(), ksd.getType(), ksd.getAlias(),
											identityProviderConfig.getEidProviderConnection().getEndpoint());
		} else {
			apiClient = new ApiCustomClient();
		}
		apiClient.setBasePath(identityProviderConfig.getEidProviderConnection().getEndpoint());

		log.info("## Using apiClient {}", apiClient.getClass().getSimpleName());
		return apiClient;
	}


	@Override
	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID, OffsetDateTime inquiryValidity) {
		AuthenticationResponse authResponse = null;

		ApiClient apiClient = getApiClient(identityProviderConfig);

		/*
		 * invokeApi parameters
		 */
		HttpMethod method = HttpMethod.GET;
		Map<String, Object> pathParams = new HashMap<>();
		pathParams.put("relyingPartyRequestID", relyingPartyRequestID);
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		Object body = null;
		HttpHeaders headerParams = setApiKeyHeaderParams(identityProviderConfig);
		MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
		MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();
		List<MediaType> accept = new LinkedList<>();
		MediaType contentType = MediaType.APPLICATION_JSON;
		String[] authNames = new String[] {};
		ParameterizedTypeReference<AuthenticationResponse> returnType = new ParameterizedTypeReference<AuthenticationResponse>() {};

		Mono<?> resApi;
		//TODO expiration period in seconds
		long timeout = getDelayInSeconds( identityProviderConfig.getExpirationPeriod(), inquiryValidity); 
		resApi = apiClient.invokeAPI(authUriPathSufix, method, pathParams, queryParams,
				body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType)
				.retryWhen(Retry.fixedDelay(repeatNums, Duration.ofSeconds(timeout/repeatNums)).filter(this::is203NotReadyError))
				;

		Object resApiBody = resApi.block();
		if (resApiBody == null) {
			log.warn("NULL AuthenticationResponse");
		} else if (resApiBody instanceof AuthenticationResponse) {
			authResponse = (AuthenticationResponse) resApiBody;
			if (authResponse.getId() == null) {
				authResponse = null;
				log.debug("AuthenticationResponse");
			} else {
				log.debug("AuthenticationResponse: {}", authResponse.getId());
			}
		} else if (resApiBody instanceof ApiClientException) {
			ApiClientException authException = (ApiClientException) resApiBody;
			log.warn("UiBackendException: {}", authException.getResponceCode());
			throw new UiBackendException(authException.getMessage(), authException, authException.getResponceCode());
		} else if (resApiBody instanceof EidProviderException) {
			EidProviderException eidException = (EidProviderException) resApiBody;
			log.warn("EidProviderException: {}", eidException.getResponceCode());
			throw new UiBackendException(eidException.getMessage(), eidException, eidException.getResponceCode());
		}

		return mapAuthentication2Login(authResponse);

	}

	
	@Override
	protected HttpHeaders setApiKeyHeaderParams(EidProviderConfig identityProviderConfig) {
		HttpHeaders headers = super.setApiKeyHeaderParams(identityProviderConfig);
		headers.putAll(identityProviderConfig.getEidProviderConnection().getCustomHeaders());
		return headers;
	}

}
