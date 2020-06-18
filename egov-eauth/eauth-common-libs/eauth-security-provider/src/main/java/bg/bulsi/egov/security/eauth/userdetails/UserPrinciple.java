package bg.bulsi.egov.security.eauth.userdetails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
public class UserPrinciple implements EauthUserPrincipal {

    @Getter
    @Setter
    private String id;
}
