package bg.bulsi.egov.idp.dto;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public class InlineResponse200 implements Serializable {

	private static final long serialVersionUID = 1L;
	private Boolean valid;
	private String message;

}
