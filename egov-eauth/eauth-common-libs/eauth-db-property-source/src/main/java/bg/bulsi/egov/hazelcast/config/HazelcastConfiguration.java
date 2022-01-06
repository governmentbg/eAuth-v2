package bg.bulsi.egov.hazelcast.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

	public static final String PROPS_MAP = "ConfigPropertiesMap";
	public static final String EMAIL_MAP = "TwoFATokenMap";
	public static final String SMS_MAP = "SmsMap";
	public static final String CALLBACK_MAP = "CallbackMap";
	
}
