package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class KeyStoreData implements Serializable{
	
	private static final long serialVersionUID = 7045781567250990044L;

	@Getter
	@Setter
	private String alias;

	@Getter
	@Setter
	private String path;

	@Getter
	@Setter
	private String type;

	@Getter
	@Setter
	private String pass;

}
