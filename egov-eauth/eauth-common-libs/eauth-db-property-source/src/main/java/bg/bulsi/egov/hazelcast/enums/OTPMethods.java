package bg.bulsi.egov.hazelcast.enums;


public enum OTPMethods implements IConfigParams {

	TOTP("totp.enabled"),
	EMAIL("email.enabled"),
	SMS("sms.enabled");

	private static final String PREFIX = "egov.eauth.dyn.tfa";
	private final String value;

	private OTPMethods(String value) {
        this.value = value;
    }

	@Override
	public String prefix() {
		return PREFIX;
	}

	@Override
	public String value() {
		return value;
	}

	@Override
	public String key() {
		return PREFIX.concat(".").concat(value);
	}

	public static String getPREFIX() {
		return PREFIX;
	}

	public static OTPMethods enumKey(String enumKey) {

		for(int i = 0; i < OTPMethods.values().length; i++) {
			if(OTPMethods.values()[i].key().equals(enumKey)){
				return OTPMethods.values()[i];
			}
		}
		return null;
	}
}
