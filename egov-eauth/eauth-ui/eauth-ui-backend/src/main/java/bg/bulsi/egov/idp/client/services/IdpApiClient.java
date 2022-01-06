package bg.bulsi.egov.idp.client.services;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import bg.bulsi.egov.eauth.eid.client.ApiClient;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeValue;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationRequest;
import bg.bulsi.egov.eauth.eid.dto.AuthenticationResponse;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.eauth.eid.dto.UserAuthData;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.exception.UiBackendException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.retry.Repeat;
import reactor.util.retry.Retry;

@Slf4j
@Component
public class IdpApiClient extends AbstractApiClient {

	private static final long serialVersionUID = 5117998434802685550L;

	@Override
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig,
			List<AssertionAttributeType> authList, AuthenticationMap auth, String requestedResource, String requestSystem) {
		configAuthRequest(identityProviderConfig, authList, auth);
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(identityProviderConfig.getEidProviderConnection().getEndpoint());

		/*
		 * invokeApi parameters
		 */
		HttpMethod method = HttpMethod.POST;
		Map<String, Object> pathParams = new HashMap<>();
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		AuthenticationRequest body = this.authenticationRequest;
		body.setRequestedResource(requestedResource);
		body.setRequestSystem(requestSystem);
		log.info("body: [{}]", body.toString());
		UserAuthData userAuthData = body.getUser();
		log.info("userAuthData: [{}]", userAuthData.toString());
		String identityString = userAuthData.getIdentityString();
		log.info("identityString: [{}]", identityString);
		String authenticationString = userAuthData.getAuthenticationString();
		log.info("authenticationString: [{}]", authenticationString);

		HttpHeaders headerParams = setApiKeyHeaderParams(identityProviderConfig);
		MultiValueMap<String, String> cookieParams = new LinkedMultiValueMap<>();
		MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<>();
		List<MediaType> accept = new LinkedList<>();
		MediaType contentType = MediaType.APPLICATION_JSON;
		String[] authNames = new String[] {};
		ParameterizedTypeReference<InquiryResult> returnType = new ParameterizedTypeReference<InquiryResult>() {
		};

		// TODO Polling
		InquiryResult inqRes = null;

		Mono<InquiryResult> resApi = apiClient.invokeAPI(inquiryUriPathSufix, method, pathParams, queryParams, body,
				headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);

		inqRes = resApi.block();
		if (inqRes!=null && StringUtils.isNotBlank(inqRes.getRelyingPartyRequestID())) {
			return inqRes;
		}else {
			throw new UiBackendException("Can't access external authentication service!", HttpStatus.UNAUTHORIZED.value());
		}
		

	}

	@Override
	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig,
			String relyingPartyRequestID, OffsetDateTime inquiryValidity) {
		AuthenticationResponse authResponse = null;
		ApiClient apiClient = new ApiClient();
		apiClient.setBasePath(identityProviderConfig.getEidProviderConnection().getEndpoint());
		long expirationTime = new Date().getTime() + (identityProviderConfig.getExpirationPeriod() * 1000);

		while (expirationTime > System.currentTimeMillis()) {

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
			ParameterizedTypeReference<AuthenticationResponse> returnType = new ParameterizedTypeReference<AuthenticationResponse>() {
			};

			Mono<AuthenticationResponse> resApi = apiClient.invokeAPI(authUriPathSufix, method, pathParams, queryParams,
					body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
			
//			authResponse= resApi.repeatWhen(Repeat
//							.onlyIf(repeatContext -> repeatContext.applicationContext() instanceof ServerWebExchange
//									&& ((ServerWebExchange) repeatContext.applicationContext()).getResponse()
//											.getStatusCode() == HttpStatus.NON_AUTHORITATIVE_INFORMATION)
//							.randomBackoff(Duration.ofSeconds(5), Duration.ofSeconds(10))
//							.timeout(Duration.ofSeconds(30))).blockLast();

			/**
			 * Returns: a mono containing the body, or a WebClientResponseException if the
			 * status code is 4xx or 5xx
			 */
			resApi	
					.repeatWhen(Repeat.onlyIf(repeatContext -> repeatContext.applicationContext() instanceof ServerWebExchange
							&& ((ServerWebExchange) repeatContext.applicationContext()).getResponse()
							.getStatusCode() == HttpStatus.NON_AUTHORITATIVE_INFORMATION)
						.randomBackoff(Duration.ofSeconds(50), Duration.ofSeconds(100))
						.timeout(Duration.ofSeconds(30)))
					.retryWhen(Retry.fixedDelay(50, Duration.ofSeconds(10)))
					.delaySubscription(Duration.ofSeconds(100))
					;
			authResponse = resApi.block(Duration.ofSeconds(300));

			// Check for Http status 203
			if (authResponse != null) {
				break;
			}

		}

		return mapAuthentication2Login(authResponse);

	}

}
