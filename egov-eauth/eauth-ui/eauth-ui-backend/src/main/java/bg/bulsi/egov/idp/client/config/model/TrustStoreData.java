package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TrustStoreData implements Serializable{
	
	private static final long serialVersionUID = 7045781567250990044L;

	private String path;
	
	private String type;

	private String pass;

}
