package bg.bulsi.egov.idp.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class InlineResponse200 implements Serializable {

	private static final long serialVersionUID = 1L;
	private Boolean valid;
	private String message;

}
