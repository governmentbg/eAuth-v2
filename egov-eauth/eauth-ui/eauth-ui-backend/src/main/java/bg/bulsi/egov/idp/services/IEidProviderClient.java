package bg.bulsi.egov.idp.services;

import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginResponse;


public interface IEidProviderClient {

	/*
	 * Връща конкретна конфигурация за даден доставчик на идентичност
	 */
	public EidProviderConfig getIdentityProviderConfig(String providerId);

	/*
	 * Тук трябва да се предвиди механизум за автоматичен резултат към UI-a с грешка
	 * 403 или 410 (за изтекло време за автентикация) времето за изчакване се
	 * получава в InquiryResult.validity
	 */
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig, AuthenticationMap authMap);

	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID);

}
