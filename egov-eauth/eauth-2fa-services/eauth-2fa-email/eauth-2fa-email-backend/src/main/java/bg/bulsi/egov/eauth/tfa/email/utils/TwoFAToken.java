package bg.bulsi.egov.eauth.tfa.email.utils;

import java.io.Serializable;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TwoFAToken implements Serializable {

    private static final long serialVersionUID = 2781108037370614779L;

    @Getter
    @Setter
    private String transactionId;

    @Getter
    @Setter
    private String eMail;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private Long timeStamp;

}
