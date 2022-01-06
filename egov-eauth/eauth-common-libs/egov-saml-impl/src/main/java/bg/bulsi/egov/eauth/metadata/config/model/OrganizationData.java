package bg.bulsi.egov.eauth.metadata.config.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrganizationData implements Serializable {

	private static final long serialVersionUID = -8616515361477185318L;
	
	private String name;
    private String displayName;
    private String url;
       
}

	