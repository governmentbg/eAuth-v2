package bg.bulsi.egov.idp.services;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import bg.bulsi.egov.eauth.eid.client.exception.ApiClientException;
import bg.bulsi.egov.eauth.eid.dto.AssertionAttributeType;
import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.client.config.model.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginResponse;
import bg.bulsi.egov.idp.exception.UiBackendException;


public interface IEidProviderClient extends Serializable {

	/*
	 * Връща конкретна конфигурация за даден доставчик на идентичност
	 */
	public EidProviderConfig getIdentityProviderConfig(String providerId);

	/*
	 * Тук трябва да се предвиди механизум за автоматичен резултат към UI-a с грешка
	 * 403 или 410 (за изтекло време за автентикация) времето за изчакване се
	 * получава в InquiryResult.validity
	 */
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig, List<AssertionAttributeType> authList, AuthenticationMap auth, String requestedResource, String requestSystem)throws UiBackendException;

	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig, String relyingPartyRequestID, OffsetDateTime inquiryValidity) throws UiBackendException;

}
