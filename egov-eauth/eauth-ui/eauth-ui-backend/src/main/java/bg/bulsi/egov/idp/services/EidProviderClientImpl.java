package bg.bulsi.egov.idp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bg.bulsi.egov.eauth.eid.dto.InquiryResult;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration;
import bg.bulsi.egov.idp.client.config.model.EidProvidersConfiguration.EidProviderConfig;
import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginResponse;
import lombok.Getter;

@Service
public class EidProviderClientImpl implements IEidProviderClient {

	@Getter
	@Autowired
	private EidProvidersConfiguration config;

	@Override
	public EidProviderConfig getIdentityProviderConfig(String providerId) {
		return config.getProviders().get(providerId);
	}

	@Override
	public InquiryResult makeAuthInquiry(EidProviderConfig identityProviderConfig, AuthenticationMap authMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoginResponse getAuthInquiryResponse(EidProviderConfig identityProviderConfig,
			String relyingPartyRequestID) {
		// TODO Auto-generated method stub
		return null;
	}

}
