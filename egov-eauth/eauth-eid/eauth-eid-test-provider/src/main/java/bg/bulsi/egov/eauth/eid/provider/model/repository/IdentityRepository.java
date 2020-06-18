package bg.bulsi.egov.eauth.eid.provider.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.bulsi.egov.eauth.eid.provider.model.Identity;

@Repository
public interface IdentityRepository extends JpaRepository<Identity, Long> {


    Optional<Identity> findByUsernameAndPassword(String username, String password);
    
    Optional<Identity> findByNid(String nid);
    
    Optional<Identity> findByEmail(String email);
    
    Optional<Identity> findByPhone(String phone);

}
