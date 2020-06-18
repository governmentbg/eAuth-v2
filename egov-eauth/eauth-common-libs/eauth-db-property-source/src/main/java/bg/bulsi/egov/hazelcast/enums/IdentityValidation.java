package bg.bulsi.egov.hazelcast.enums;

public enum IdentityValidation implements IConfigParams {
	
	GRAO("grao"), 
	AV("av");

	private static final String PREFIX = "identity.validation";
	private final String value;
	
	private IdentityValidation(String value) {
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
