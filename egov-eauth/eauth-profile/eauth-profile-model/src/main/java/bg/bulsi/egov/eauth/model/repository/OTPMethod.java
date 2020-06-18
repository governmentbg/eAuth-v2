package bg.bulsi.egov.eauth.model.repository;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import javax.persistence.*;
import java.io.Serializable;
import bg.bulsi.egov.eauth.model.Preferred2FA;


@Entity
@Table(name = "otp_method", schema = "profile")
@EqualsAndHashCode(callSuper = false)
public class OTPMethod implements Persistable<Long>, Serializable {

    private static final long serialVersionUID = -7281382246285301337L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_generator")
    @Getter
    @Setter
    private Long id;


    @Getter
    @Setter
    private Preferred2FA preferred2FA;

    @Getter
    @Setter
    private boolean isActive;

    @Override
    public boolean isNew() {
        return getId() == null;
    }

}
