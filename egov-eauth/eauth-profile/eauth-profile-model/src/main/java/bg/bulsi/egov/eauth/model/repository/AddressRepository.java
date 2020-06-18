package bg.bulsi.egov.eauth.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.bulsi.egov.eauth.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
