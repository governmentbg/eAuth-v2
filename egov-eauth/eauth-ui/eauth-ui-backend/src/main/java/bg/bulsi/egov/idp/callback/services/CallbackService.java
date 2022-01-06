package bg.bulsi.egov.idp.callback.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CallbackService {
	
	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	// 2 * 60 * 1000 ms
	@Value("${egov.eauth.sys.backend.poll.timeout.msec:120000}")
	private long pollingTimeout;
	
	@Value("${egov.eauth.sys.backend.poll.interval.msec:500}")
	private long pollingInterval;
	

	public AuthenticationCallbackResult polling(String relyingPartyRequestID) {
		log.info("polling by relyingPartyRequestID: [{}]", relyingPartyRequestID);
		logSessiongId();
		AuthenticationCallbackResult authResult = null;
		
		IMap<String, AuthenticationCallbackResult> callbackMap = hazelcastInstance.getMap(HazelcastConfiguration.CALLBACK_MAP);
		log.debug("callback map is empty: [{}]", callbackMap.isEmpty());
		
		long start = System.currentTimeMillis();
		while ((System.currentTimeMillis() - start) <= pollingTimeout && authResult == null) {
			try {
				Thread.sleep(pollingInterval);
				log.debug("polling...[" + (System.currentTimeMillis() - start) + "] ms");
			} catch (InterruptedException e) {
				log.error("error: {}", e);
			}
			authResult = callbackMap.get(relyingPartyRequestID);
		}
		callbackMap.remove(relyingPartyRequestID); // clean up?
		return authResult;
	}
	
	private void logSessiongId() {
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String sessionId = attr.getSessionId();
		log.debug("sessionId: [{}]", sessionId);
	}
}
