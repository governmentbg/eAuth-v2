package bg.bulsi.egov.idp.callback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import bg.bulsi.egov.idp.callback.events.CallbackEventPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/idp")
public class CallbackController {
	
	@Autowired
	private CallbackEventPublisher publisher;

	
	@PostMapping(value = "/callback/{vendorId}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<HttpStatus> postCallback(
			@PathVariable("vendorId") String vendorId, 
			@RequestBody AuthenticationCallbackResult authenticationCallback) {
		
		log.info("vendorId: [{}]", vendorId);
		log.info("received AuthenticationCallbackResult: {}", authenticationCallback.toString());
		
		// publish auth response received for requested relyingPartyID
		publisher.publishCallbackEvent(authenticationCallback);
		
		return ResponseEntity.ok(HttpStatus.OK);
	}
}
