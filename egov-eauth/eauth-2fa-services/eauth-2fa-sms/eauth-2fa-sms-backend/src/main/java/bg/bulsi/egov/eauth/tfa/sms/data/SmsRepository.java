package bg.bulsi.egov.eauth.tfa.sms.data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import bg.bulsi.egov.eauth.tfa.sms.model.Sms;

public interface SmsRepository extends CrudRepository<Sms, Long> {

	Optional<Sms> findByTransactionIdAndCode(String transactionId, String code);
	
	@Query("select s from Sms s where s.createDate <= :createDateTime")
	List<Sms> findAllWithCreateDateBefore(@Param("createDateTime") Date createDate);
	
	@Modifying
	@Query("delete from Sms s where s.createDate <= :createDateTime")
	void deleteAllWithCreateDateBefore(@Param("createDateTime") Date createDate);
}
