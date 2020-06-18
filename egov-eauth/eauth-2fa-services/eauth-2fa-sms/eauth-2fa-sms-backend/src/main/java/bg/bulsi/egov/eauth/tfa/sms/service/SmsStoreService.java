package bg.bulsi.egov.eauth.tfa.sms.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import bg.bulsi.egov.eauth.tfa.sms.model.Sms;
import bg.bulsi.egov.eauth.tfa.util.SmsUtils;
import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SmsStoreService {

	private final IMap<String, Sms> store;

	public SmsStoreService(final HazelcastInstance hazelcastInstance) {
		this.store = hazelcastInstance.getMap(HazelcastConfiguration.SMS_MAP);
	}

	public void save(Sms sms) {
		store.put(sms.getTransactionId(), sms);
	}

	public synchronized void update(Sms sms) {
		String transactionId = sms.getTransactionId();
		if (store.containsKey(transactionId)) {
			store.put(transactionId, sms);
		}
	}

	public synchronized boolean validate(String transactionId, String code, long expirationTimeInSeconds) {
		boolean valid = false;
		if (store.containsKey(transactionId)) {
			Sms sms = store.get(transactionId);
			if (sms.getCode().equals(code) && SmsUtils.codeNotExpired(sms.getCreateDate(), expirationTimeInSeconds)) {
				valid = true;
			}
		}
		return valid;
	}

	public void print() {
		store.forEach((key, value) -> log.info("{} : {}", key, value));
	}
}
