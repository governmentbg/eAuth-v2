package bg.bulsi.egov.idp.nap.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.idp.exception.UiBackendException;
import bg.bulsi.egov.idp.nap.model.IdentityType;
import bg.bulsi.egov.idp.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class NapController {
	
	private static final String PROVIDER_ID_SESSION_ATTR_NAME = "ProviderIdRequest";
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private IdpConfigurationProperties idpConfig;
	
	@Value("${nap.provider.identity.endpoint.url}")
	private String url;
	
	@Value("${nap.provider.identity.endpoint.unique_system_id.name}")
	private String param;
	
	@Value("${nap.provider.identity.endpoint.unique_system_id.value}")
	private String systemId;
	
	@Value("${nap.provider.identity.endpoint.unique_system_id.key}")
	private String key;
	
	@Value("${nap.provider.identity.default-name}")
	private String defaultName;
	
	// TODO: remove if not used?
	@Value("${nap.provider.id}")
	private String napProviderId;
	
	/**
	 * redirects to NAP PIK authentication
	 * @return HTTP Status 302 with Location
	 */
	@GetMapping("/napeid-pik") 
	public ResponseEntity<Void> redirectToNap(@RequestParam(value = "providerId") String providerId) {
		String query = "&" + param + "=" + systemId;
		log.debug("query: [{}]", query);
		
		log.debug("providerId: [{}]", providerId);
		saveProviderIdToSession(providerId);
		
		logSessiongId();
		
		return ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(url + query))
				.build();
	}

	/**
	 * Test url to redirect the test JWT(in config) as query param 
	 * to the /napeid endpoint
	 */
	@GetMapping("/napeid-test")
	public ResponseEntity<Void> redirectWithTestJwt(
			@Value("${nap.provider.identity.redirect.url}") String redirectUrl,
			@Value("${nap.provider.identity.redirect.jwt}") String jwt) {
		
		// redirectUrl = "http://localhost:8080/napeid/";
		String query = "?jwt=" + jwt;
		log.debug("query: [{}]", query);
		
		logSessiongId();
		
		return ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(redirectUrl + query))
				.build();
	}
	
	@GetMapping("/napeid") 
	public String receiveFromNap(@RequestParam(value = "jwt") String jwt, Model model) throws UnmarshallingException {
		log.debug("jwt: [{}]", jwt);
		log.debug("jwt verified: [{}]", JwtUtil.verifyJWT(jwt));
		
		String base64Key = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
		log.debug("key: [{}] => encoded: [{}]", key, base64Key);
		
		Claims claims = null;
		try {
			claims = JwtUtil.decodeJWT(jwt, base64Key);
		} catch (SignatureException e) {
			throw new UiBackendException("Signature exception", e);
		} catch (MalformedJwtException e) {
			throw new UiBackendException("Incorrect JWT", e);
		}
		
		model.addAttribute("jwt", jwt);
		
		int typeCode = claims.get("ty", Integer.class);
		IdentityType type = IdentityType.getIdentityByType(typeCode);
		model.addAttribute("typeCode", typeCode);
		log.debug("jwt claims typeCode: [{}] => IdentityType: [{}]", typeCode, type);
		
		String id = claims.get("id", String.class);
		model.addAttribute("id", id);
		log.debug("jwt claims id: [{}]", id);
		
		Date exp = claims.getExpiration();
		String expDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(exp);
		// model.addAttribute("expDate", expDate);
		log.debug("jwt claims exp: [{}]:", expDate);
		
		logSessiongId();
		
        String loginUrl = idpConfig.getPathPrefix() + "/api/idp/login";
        
        String providerId = getProviderIdFromSession(); // old: napProviderId
        
        // POST is executed in html form
        // doPost(id, providerId, loginUrl);
        
		model.addAttribute("action", loginUrl);
		model.addAttribute("providerId", providerId);
		model.addAttribute("defaultName", defaultName);
		
		return "nap-authentication";
	}

	private void logSessiongId() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String sessionId = attr.getSessionId();
		log.debug("sessionId: [{}]", sessionId);
	}
	
	private void saveProviderIdToSession(String providerId) {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		HttpSession session = request.getSession(false);
		session.setAttribute(PROVIDER_ID_SESSION_ATTR_NAME, providerId);
		log.debug("ProviderIdRequest: {} saved to session.", providerId);
	}
	
	private String getProviderIdFromSession() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		HttpSession session = request.getSession(false);
		String providerId = (String) session.getAttribute(PROVIDER_ID_SESSION_ATTR_NAME);
		session.removeAttribute(PROVIDER_ID_SESSION_ATTR_NAME); // clear session attr
		log.debug("ProviderIdRequest: {} removed from session.", providerId);
		return providerId;
	}
	
	/**
	 * do POST /api/idp/login -> ExternalIdpAuthenticationProcessingFilter
	 * request param name: '__egn__'
	 * request param name: 'providerId' 
	 */
	private void doPost(String identifier, String providerId, String loginUrl) {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("__egn__", identifier);
        params.add("providerId", providerId); // 'nap_pik', 'nap_pik_tfa'
        
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		
		String baseUrl = "http://localhost:8080";
		
		restTemplate.postForEntity(baseUrl + loginUrl, request, String.class);
	}
}
