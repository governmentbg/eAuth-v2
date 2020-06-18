package bg.bulsi.egov.eauth.model;


public enum Preferred2FA {
	
    EMAIL("EMAIL"),
    
    TOTP("TOTP"),
    
    SMS("SMS");
	
	public final String label;
	
	private Preferred2FA(String label) {
		this.label = label;
	}
	
}
