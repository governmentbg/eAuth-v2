package soap.clientregix.requests.grao;

import soap.clientregix.requests.Operation;

public enum GraoOperation implements Operation {
    PERMANENT_ADDRESS_SEARCH("TechnoLogica.RegiX.GraoPNAAdapter.APIService.IPNAAPI.PermanentAddressSearch"),
    PERSON_DATA_SEARCH("TechnoLogica.RegiX.GraoNBDAdapter.APIService.INBDAPI.PersonDataSearch"),
	VALID_PERSON_SEARCH("TechnoLogica.RegiX.GraoNBDAdapter.APIService.INBDAPI.ValidPersonSearch");
	
    private final String key;
    
    private GraoOperation(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
