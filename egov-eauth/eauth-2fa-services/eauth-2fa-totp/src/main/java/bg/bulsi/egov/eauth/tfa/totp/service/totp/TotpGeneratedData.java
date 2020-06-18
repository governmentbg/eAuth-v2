package bg.bulsi.egov.eauth.tfa.totp.service.totp;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
public class TotpGeneratedData {

    @Getter

    private String user;

    @Getter

    private String secret;

    @Getter

    private String generateKeyUri;

}
