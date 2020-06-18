package bg.bulsi.egov.eauth.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.bulsi.egov.eauth.model.ekkate.Ekatte;

@Repository
public interface EkatteRepository extends JpaRepository<Ekatte, String> {

    List<Ekatte> findByPlaceContainingOrderByPlace(String place);
    List<Ekatte> findAllByPlaceContainingIgnoreCaseOrderByPlace(String place);

    Optional<Ekatte> findByEkatteId(String id);

}

