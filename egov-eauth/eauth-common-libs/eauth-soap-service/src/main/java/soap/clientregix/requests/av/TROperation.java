package soap.clientregix.requests.av;

import soap.clientregix.requests.Operation;

public enum TROperation implements Operation {
    GET_ACTUAL_STATE("TechnoLogica.RegiX.AVTRAdapter.APIService.ITRAPI.GetActualState "),
    GET_VALID_UIC_INFO("TechnoLogica.RegiX.AVTRAdapter.APIService.ITRAPI.GetValidUICInfo"),
    PERSON_IN_COMPANIES_SEARCH("TechnoLogica.RegiX.AVTRAdapter.APIService.ITRAPI.PersonInCompaniesSearch");
    
    private final String key;
    
    private TROperation(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
