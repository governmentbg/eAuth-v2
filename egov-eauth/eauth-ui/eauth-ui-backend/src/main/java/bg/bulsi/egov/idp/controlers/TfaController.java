package bg.bulsi.egov.idp.controlers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import bg.bulsi.egov.hazelcast.enums.OTPMethods;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.idp.dto.AuthTimeout;
import bg.bulsi.egov.idp.dto.CodeRequest;
import bg.bulsi.egov.idp.dto.EnabledOTPMethod;
import bg.bulsi.egov.idp.dto.IdentityProvider;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.dto.OTPMethod;
import bg.bulsi.egov.idp.dto.OTPresponse;
import bg.bulsi.egov.idp.dto.RegisterRequest;
import bg.bulsi.egov.idp.dto.SecretMetadata;
import bg.bulsi.egov.idp.services.TfaService;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/idp")
public class TfaController {

	private final TfaService tfaService;
	private final ProviderService providerService;
	private final HazelcastService hazelcastService;

	public TfaController(TfaService tfaService, ProviderService providerService, HazelcastService hazelcastService) {
		this.tfaService = tfaService;
		this.providerService = providerService;
		this.hazelcastService = hazelcastService;
	}

	@GetMapping("/providers")
	public ResponseEntity<List<IdentityProvider>> listProviders() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			// TODO - трябва да е exception който се обработва от @ControllerAdvice
			throw new RuntimeException("Missing authentication object");
		}

		LevelOfAssurance spRequestLoa = providerService.authnRequestlevelOfAssurance();
		
		List<IdentityProvider> providers = providerService.list(spRequestLoa);

		return ResponseEntity.ok().body(providers);
	}

	@GetMapping("/auth-timeout")
	public ResponseEntity<AuthTimeout> authTimeout() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			// TODO - трябва да е exception който се обработва от @ControllerAdvice
			throw new RuntimeException("Missing authentication object");
		}

		AuthTimeout timeout = providerService.authenticationTimeout();

		return ResponseEntity.ok().body(timeout);
	}
	
	@GetMapping(value = "/enabled-otp-methods", produces = "application/json")
	public ResponseEntity<List<OTPMethods>> listOfAllEnabledOtpMethods() {
		log.info("listOfAllEnabledOtpMethods!");
		List<OTPMethods> list = tfaService.allEnabledOtpMethods();

		return ResponseEntity.ok().body(list);
	}

	@GetMapping("/tfa/send")
	public ResponseEntity<OTPresponse> sendCode() {
		log.info("send!");
		OTPresponse response = tfaService.send();
		if (response == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}

		return ResponseEntity.ok(response);
	}

	@PutMapping("/tfa/resend")
	public ResponseEntity<OTPresponse> resendCode(@RequestBody CodeRequest body) {
		log.info("resend!");
		OTPMethod method = body.getNewCodeType();
		OTPresponse response = tfaService.send(method);
		if (response == null) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/tfa/otpmethods")
	public ResponseEntity<List<EnabledOTPMethod>> listOfEnabledUserOtpMethods() {
		log.info("listOfEnabledOtpMethods!");
		List<EnabledOTPMethod> list = tfaService.enabledUserOtpMethods();

		return ResponseEntity.ok().body(list);
	}

	@PutMapping("/tfa/register-user")
	public ResponseEntity<SecretMetadata> registerUser(@RequestBody RegisterRequest body) {
		OTPMethod method = body.getMethod();
		log.info("registerUser! method: [{}]", method);
		if (method == OTPMethod.TOTP) {
			return tfaService.generateTotpSecret();
		}

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
	}

	@PutMapping("/dbreload")
	@ResponseBody
	public void reloadDbPropertiesInHazelcast(@RequestHeader("Reload") boolean reload) {
		if (reload) {
			log.info("reload");
			hazelcastService.initializePropertiesMap(true);
		}
	}
}
