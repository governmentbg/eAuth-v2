package bg.bulsi.egov.idp.callback.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CallbackEventListener {
	
	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	
	@Async
	@EventListener
	public void handleCallbackEvent(CallbackEvent event) {
		AuthenticationCallbackResult authCallback = event.getAuthenticationCallback();
		String relyingPartyRequestID = authCallback.getRelyingPartyRequestID();
		log.debug("Received callback event for relyingPartyRequestID: [{}]", relyingPartyRequestID);
		
		IMap<String, AuthenticationCallbackResult> callbackMap = hazelcastInstance.getMap(HazelcastConfiguration.CALLBACK_MAP);
		callbackMap.put(relyingPartyRequestID, authCallback);
	}
}
