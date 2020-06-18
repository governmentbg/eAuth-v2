package bg.bulsi.ocspclient.common;

import bg.bulsi.ocspclient.exception.QESProvidersException;

import java.util.HashMap;
import java.util.Map;

public enum QESProviders {
	
	STAMP_IT("STAMP_IT", "Information Services Plc.", "StampIt"),
	INFO_NOTARY("INFO_NOTARY", "InfoNotary PLC", "InfoNotary"),
	SPEKTAR("SPEKTAR", "Spektar JSC, B:831431323", "Spektar"),
	SEP("SEP", "System for Electronic Payments /SEP Bulgaria JSC", "Sep"),
	B_TRUST("B_TRUST", "BORICA - BANKSERVICE AD, EIK 201230426", "BTrust");
	
	private String enumName;
	private String organizationName;
	private String shortName;
	
	private static Map<String, String> providersMap = new HashMap<String, String>();
		
	
	private QESProviders(String enumName, String organizationName, String shortName) {
		this.enumName = enumName;
		this.organizationName = organizationName;
		this.shortName = shortName;
	}
	
	
	static {
		initProvidersMap();
	}
	
	
	private static void initProvidersMap() {
		QESProviders[] qesProviders = QESProviders.values();
		
		for (int i = 0; i < qesProviders.length; i++) {
			QESProviders qesProvider = qesProviders[i];
			
			String enumName = qesProvider.getEnumName();
			String organizationName = qesProvider.getOrganizationName();
			
			providersMap.put(organizationName, enumName);
		}
	}
	

	public String getEnumName() {
		return this.enumName;
	}

	
	public String getShortName() {
		return this.shortName;
	}

	
	public String getOrganizationName() {
		return this.organizationName;
	}
	
	
	public static QESProviders getByOrganizationName(String organizationName) throws QESProvidersException {
		if (organizationName == null) {
			throw new QESProvidersException("Issuer organization name must not be NULL!");
		}

		String enumName = providersMap.get(organizationName);
		
		if (enumName == null) {
			throw new QESProvidersException("Issuer organization '" + organizationName + "' is not known QES provider, or check SELF_SIGNED_PROVIDER_LIST in configuration!");
		}
		
		QESProviders provider = QESProviders.valueOf(enumName);
		return provider;
	}
	
}
