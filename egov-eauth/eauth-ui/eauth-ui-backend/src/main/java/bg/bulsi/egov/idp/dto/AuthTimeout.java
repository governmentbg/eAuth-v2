package bg.bulsi.egov.idp.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthTimeout implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private long expirationTimestamp;

}
