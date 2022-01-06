package bg.bulsi.egov.idp.client.config.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import bg.bulsi.egov.idp.dto.AuthenticationAttribute;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
public class ProviderAuthAttribute extends AuthenticationAttribute implements Serializable {

	private static final long serialVersionUID = -7304199837524898345L;

	@Getter
	@Setter
	@NotBlank
	private Eid eId;

}