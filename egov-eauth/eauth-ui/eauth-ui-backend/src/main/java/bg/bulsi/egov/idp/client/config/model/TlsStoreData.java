package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class TlsStoreData implements Serializable{
	
	private static final long serialVersionUID = 7045781567250990044L;

	private TrustStoreData trustStoreData;
	
	private KeyStoreData keyStoreData;

	private String url;

}
