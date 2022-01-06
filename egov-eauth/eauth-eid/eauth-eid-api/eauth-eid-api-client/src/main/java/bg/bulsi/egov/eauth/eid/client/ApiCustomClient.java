package bg.bulsi.egov.eauth.eid.client;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import bg.bulsi.egov.eauth.eid.client.exception.ApiClientException;
import bg.bulsi.egov.eauth.eid.dto.CommonAuthException;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class ApiCustomClient extends ApiClient {

	/**
	 * Prepare to create Custom Builder (w/o TLS)
	 */
	public ApiCustomClient() {
		// TODO Auto-generated constructor stub
        dateFormat = createDefaultDateFormat();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        super.webClient = ApiClient.buildWebClient(mapper);
        init();
    }

	/**
	 * Prepare to create Custom Builder (w/o TLS)
	 */
    public ApiCustomClient(ObjectMapper mapper, DateFormat format) {
        this(buildWebClient(mapper.copy()), format);
    }

	/**
	 * Prepare to create Custom Builder (w/o TLS)
	 */
    public ApiCustomClient(WebClient webClient, ObjectMapper mapper, DateFormat format) {
        this(Optional.ofNullable(webClient).orElseGet(() -> ApiClient.buildWebClient(mapper.copy())), format);
    }

	/**
	 * Prepare to create Custom Builder (w/o TLS)
	 */
    private ApiCustomClient(WebClient webClient, DateFormat format) {
        super.webClient = webClient;
        dateFormat = format;
        init();
    }
	
	/**
	 * Prepare to create Custom Builder (w/ TLS)
	 */
	public ApiCustomClient(Builder customWebClientBuilder, String url) {

		super.dateFormat = createDefaultDateFormat();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(dateFormat);
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNullableModule jnm = new JsonNullableModule();
		mapper.registerModule(jnm);

		super.webClient = buildWebClientSsl(mapper, customWebClientBuilder, url);

		super.init();
	}


	/**
	 * Prepare to create Client w/ TLS
	 */
	public ApiCustomClient(KeyStore trustStore, String trustStorePass, String trustStoreType, KeyStore keyStore, String keyStorePass, String keyStoreType, String keyAlias,
			String url) {

		super.dateFormat = createDefaultDateFormat();
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(dateFormat);
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNullableModule jnm = new JsonNullableModule();
		mapper.registerModule(jnm);

		super.webClient = buildWebClientSsl(mapper, trustStore, trustStorePass, trustStoreType, keyStore, keyStorePass, keyStoreType, keyAlias, url);

		super.init();
	}


	/**
	 * Build the RestTemplate used to make TLS requests.
	 * 
	 * @return RestTemplate
	 */
	public WebClient buildWebClientSsl(
			ObjectMapper mapper,
			KeyStore trustStore, String trustStorePass, String trustStoreType, KeyStore keyStore, String keyStorePass, String keyStoreType, String keyAlias,
			String url) {

		WebClient.Builder webClientBuilder = configureWebClientSslBuilder(trustStore, trustStorePass, trustStoreType, keyStore, keyStorePass, keyStoreType, keyAlias);

		webClientBuilder = webClientBuilder.baseUrl(url);

		{ // From Build the RestTemplate used to make HTTP requests.
			ExchangeStrategies strategies = ExchangeStrategies
					.builder()
					.codecs(clientDefaultCodecsConfigurer -> {
						clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
						clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
					}).build();

			webClientBuilder = webClientBuilder.exchangeStrategies(strategies);
		}

		return webClientBuilder.build();
	}


	public WebClient buildWebClientSsl(ObjectMapper mapper, WebClient.Builder webClientBuilder, String url) {

		webClientBuilder = webClientBuilder.baseUrl(url);

		{ // From Build the RestTemplate used to make HTTP requests.
			ExchangeStrategies strategies = ExchangeStrategies
					.builder()
					.codecs(clientDefaultCodecsConfigurer -> {
						clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON));
						clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON));
					}).build();

			webClientBuilder = webClientBuilder.exchangeStrategies(strategies);
		}

		return webClientBuilder.build();

	}


	public Builder configureWebClientSslBuilder(
			KeyStore trustStore, String trustStorePass, String trustStoreType,
			KeyStore keyStore, String keyStorePass, String keyStoreType, String keyAlias) {

		WebClient.Builder webClient;

		SslContext sslContext;

		PrivateKey privateKey;

		X509Certificate[] certificates;

		try {

			List<Certificate> certificateList = Collections.list(trustStore.aliases())
					.stream()
					.filter(t -> {
						try {
							return trustStore.isCertificateEntry(t);
						} catch (KeyStoreException e1) {
							throw new RuntimeException("Error reading truststore", e1);
						}
					})
					.map(t -> {
						try {
							return trustStore.getCertificate(t);
						} catch (KeyStoreException e2) {
							throw new RuntimeException("Error reading truststore", e2);
						}
					})
					.collect(Collectors.toList());

			certificates = certificateList.toArray(new X509Certificate[certificateList.size()]);

			privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyStorePass.toCharArray());

			Certificate[] certChain = keyStore.getCertificateChain(keyAlias);

			X509Certificate[] x509CertificateChain = Arrays.stream(certChain)
					.map(certificate -> (X509Certificate) certificate)
					.collect(Collectors.toList())
					.toArray(new X509Certificate[certChain.length]);

			sslContext = SslContextBuilder.forClient()
					.keyManager(privateKey, keyStorePass, x509CertificateChain)
					.trustManager(certificates)
					.build();

			HttpClient httpClient = HttpClient.create()
					.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

			ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

			webClient = WebClient.builder().clientConnector(connector);

		} catch (KeyStoreException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
			throw new RuntimeException(e);
		}

		return webClient;
	}


	@Override
	public <T> Mono<T> invokeAPI(
			String path, HttpMethod method, Map<String, Object> pathParams, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams,
			MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType, String[] authNames,
			ParameterizedTypeReference<T> returnType)
			throws RestClientException {

		final WebClient.RequestBodySpec requestBuilder = prepareRequest(path, method, pathParams, queryParams, body, headerParams, cookieParams, formParams, accept, contentType,
				authNames);

		return requestBuilder.retrieve()
				.onStatus(httpStatus -> httpStatus.value()==203, response -> {
					log.error("User ERROR still not authenticated with status code {} and body:\n{}",response.statusCode(), response.bodyToMono(CommonAuthException.class).toString());
	                throw new ApiClientException(ResponceCodes.HTTP_203.getMsg(), ResponceCodes.HTTP_203.getCode()); 
	            })
				.onStatus(httpStatus -> httpStatus.is4xxClientError(), response -> {
            		log.error("Client ERROR with status code {} and body:\n{}",response.statusCode(), response.bodyToMono(CommonAuthException.class).toString());
					throw new ApiClientException(ResponceCodes.HTTP_400.getMsg(), response.createException().block(), ResponceCodes.HTTP_400.getCode());
				})
				.onStatus(httpStatus -> httpStatus.is5xxServerError(), response -> {
            		log.error("Server ERROR with status code {} and body:\n{}",response.statusCode(), response.bodyToMono(String.class).toString());
					throw new ApiClientException(ResponceCodes.HTTP_500.getMsg(), response.createException().block(), ResponceCodes.HTTP_500.getCode());
				})
//				.onStatus(httpStatus -> httpStatus.value()==203, response -> Mono.empty())
//				.onStatus(httpStatus -> httpStatus.value()==203, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_203.getMsg(), ResponceCodes.HTTP_203.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==400, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_400.getMsg(), response.createException().block(), ResponceCodes.HTTP_400.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==401, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_401.getMsg(), response.createException().block(), ResponceCodes.HTTP_401.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==404, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_404.getMsg(), response.createException().block(), ResponceCodes.HTTP_404.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==405, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_405.getMsg(), response.createException().block(), ResponceCodes.HTTP_405.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==409, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_409.getMsg(), response.createException().block(), ResponceCodes.HTTP_409.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==500, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_500.getMsg(), response.createException().block(), ResponceCodes.HTTP_500.getCode())))
//				.onStatus(httpStatus -> httpStatus.value()==501, response -> Mono.error(new ApiClientException(ResponceCodes.HTTP_501.getMsg(), response.createException().block(), ResponceCodes.HTTP_501.getCode())))
				.bodyToMono(returnType)
				;
	}

	// @Override
	// public <T> Flux<T> invokeFluxAPI(
	// String path, HttpMethod method, Map<String, Object> pathParams, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams,
	// MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType, String[] authNames,
	// ParameterizedTypeReference<T> returnType) throws RestClientException {
	//
	// final WebClient.RequestBodySpec requestBuilder = prepareRequest(path, method, pathParams, queryParams, body, headerParams, cookieParams, formParams, accept, contentType,
	// authNames);
	// ResponseSpec responseSpec = requestBuilder.retrieve();
	//
	// Function<? super Throwable, ? extends Publisher<? extends T>> fallback = (ex) -> {
	//
	// if (ex instanceof WebClientResponseException) {
	//
	// WebClientResponseException wcex = (WebClientResponseException) ex;
	// RestClientException rcex = (RestClientException) ex;
	//
	// if (400 == wcex.getRawStatusCode()) {
	// return Flux.error(new ApiClientException("", wcex, 400));
	// } else if (404 == wcex.getRawStatusCode()) {
	// return Flux.error(new ApiClientException("", wcex, 404));
	// } else {
	// return Flux.error(new ApiClientException("", wcex, 500));
	// }
	//
	// }
	//
	// if (ex instanceof RestClientException) {
	//
	// RestClientException rcex = (RestClientException) ex;
	//
	// return Flux.error(new ApiClientException("", rcex, 400));
	// }
	//
	// return Flux.error(new ApiClientException("", ex, 500));
	// };
	//
	//
	// return responseSpec.bodyToFlux(returnType).onErrorResume(fallback);
	// }


	public enum ResponceCodes {

		HTTP_200(200, "Request successfuly processed"),
		HTTP_203(203, "Request is accepted but still in processing"),
		HTTP_400(400, "Bad Request, user can change it and resubmit new correct request"),
		HTTP_401(401, "API key is missing or invalid"),
		HTTP_404(404, "The specified resource was not found"),
		HTTP_405(405, "Invalid request or not allowed method"),
		HTTP_409(409, "The user might be able to resolve the conflict and resubmit the request"),
		HTTP_500(500, "The server encountered an unexpected exception"),
		HTTP_501(501, "Not Implemented");


		@Getter
		int code;
		@Getter
		String msg;


		ResponceCodes(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}

	}

}
