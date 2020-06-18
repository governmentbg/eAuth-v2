package bg.bulsi.egov.hazelcast.enums;

public enum EIDProviders implements IConfigParams {

	NOI("noi"),
	NAP("nap"),
	BORICA_CQES("borica.cqes"),
	EVROTRUST_CQES("evrotrust.cqes"),
	TEST_EID("test.eid");

	private static final String PREFIX = "eid.providers";
	private final String value;
	
	private EIDProviders(String value) {
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
}
