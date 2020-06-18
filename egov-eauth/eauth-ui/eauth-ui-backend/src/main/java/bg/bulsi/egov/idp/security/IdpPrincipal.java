package bg.bulsi.egov.idp.security;


import bg.bulsi.egov.idp.dto.IdentityAttributes;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdpPrincipal implements Principal, Serializable {

  private static final long serialVersionUID = -5910673831137543372L;

  private String providerId = null;
  private LevelOfAssurance loa = null;
  private List<IdentityAttributes> attributes = new ArrayList<>();

  @Override
  public String getName() {
    return providerId;
  }

}
