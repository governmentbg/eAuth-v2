package bg.bulsi.egov.idp.nap.model;

import lombok.Getter;

public enum IdentityType {

	EGN(0,"ЕГН"),
	EIK(1,"ЕИК"),
	LNCH(2,"ЛНЧ");


	@Getter
	int type;
	
	@Getter
	String desc;


	private IdentityType(int type, String desc) {
		this.type = type;
		this.desc = desc;
	}
	
	public static IdentityType getIdentityByType(int ty) {
		IdentityType identityType = null;
		
		if (ty == 0) {
			identityType = EGN;
		} else if (ty == 1) {
			identityType = EIK;
		} else if (ty == 2) {
			identityType = LNCH;
		} 

		return identityType;
	}

}
