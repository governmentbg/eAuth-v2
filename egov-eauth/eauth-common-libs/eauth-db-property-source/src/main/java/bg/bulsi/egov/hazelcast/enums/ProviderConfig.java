package bg.bulsi.egov.hazelcast.enums;

public enum ProviderConfig {

	API_KEY("api.key"),
	END_POINT("endpoint"), 
	LOA("level.of.assurance"), 
	CLIENT_CERTIFICATE("client.cert"), 
	SSL("ssl");
	
	private final String value;

	private ProviderConfig(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}
}
