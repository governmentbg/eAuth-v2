package bg.eauth.admin.repository;

import bg.eauth.admin.domain.Config;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * Spring Data  repository for the Config entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigRepository extends JpaRepository<Config, String> {
    Optional<Config> findByPropertyKey(String var1);
}
