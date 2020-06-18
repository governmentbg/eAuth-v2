package bg.bulsi.egov.eauth.model.repository;

import bg.bulsi.egov.eauth.model.Preferred2FA;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OtpRepository  extends JpaRepository<OTPMethod, String> {

     List<OTPMethod> findByIsActive(boolean isActive);
}
