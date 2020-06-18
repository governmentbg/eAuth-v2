package bg.bulsi.egov.idp.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CodeData implements Serializable {

  private static final long serialVersionUID = 1L;

  private OTPMethod method;

  private String code;

  private String tId;

}
