package bg.bulsi.egov.eauth.audit.model;

import lombok.Getter;

/**
 * Enumerated auditing project application names
 */
public enum Origins {
	
	EAUTH_2FA_EMAIL("email_2fa"),
	EAUTH_2FA_SMS("sms_2fa"),
	EAUTH_2FA_TOTP("totp_2fa"),
	EAUTH_EID_TEST_PROVIDER("test_eid_provider"),
	EAUTH_PROFILE_BACKEND("profile_2fa"),
	EAUTH_TEST_SP("test_sp"),
	EAUTH_UI_ADMIN("eAuth_admin"),
	EAUTH_UI_BACKEND("eAuth_IdP");
	
	@Getter
	String applicationName;
	
	private Origins(String name) {
		this.applicationName = name;
	}
	
}
