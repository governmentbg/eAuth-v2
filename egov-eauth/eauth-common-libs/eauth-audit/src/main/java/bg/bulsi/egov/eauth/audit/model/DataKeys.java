package bg.bulsi.egov.eauth.audit.model;

import lombok.Getter;

public enum DataKeys {
	
	SOURCE("source"),
    HTTP_REMOTE_ADDRESS("remoteAddress"),
    HTTP_SESSION_ID("sessionId"),
    DB_MODEL("dbModel");

	@Getter
	String dataKey;
	
	private DataKeys(String dataKey) {
		this.dataKey = dataKey;
	}

}
