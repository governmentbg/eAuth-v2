package bg.bulsi.egov.idp.callback.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CallbackEventPublisher {

	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	public void publishCallbackEvent(final AuthenticationCallbackResult authenticationCallback) {
		log.debug("Publishing callback event for relyingPartyRequestID: [{}]", authenticationCallback.getRelyingPartyRequestID());
		
		CallbackEvent event = new CallbackEvent(this, authenticationCallback);
		publisher.publishEvent(event);
	}
}
