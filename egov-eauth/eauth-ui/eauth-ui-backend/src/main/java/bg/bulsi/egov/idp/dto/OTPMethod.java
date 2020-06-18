package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets OTPMethod
 */
public enum OTPMethod {
	EMAIL("EMAIL"), 
	TOTP("TOTP"), 
	SMS("SMS");

	private String value;

	OTPMethod(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static OTPMethod fromValue(String text) {
		for (OTPMethod b : OTPMethod.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
