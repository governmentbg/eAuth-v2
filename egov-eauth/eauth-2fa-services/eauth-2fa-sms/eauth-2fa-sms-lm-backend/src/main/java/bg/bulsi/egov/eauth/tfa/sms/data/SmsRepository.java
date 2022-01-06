package bg.bulsi.egov.eauth.tfa.sms.data;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import bg.bulsi.egov.eauth.tfa.sms.model.SmsLM;

public interface SmsRepository extends CrudRepository<SmsLM, Long> {

	Optional<SmsLM> findByTransactionIdAndCode(String transactionId, String code);
	
	@Query("select s from SmsLM s where s.createDate <= :createDateTime")
	List<SmsLM> findAllWithCreateDateBefore(@Param("createDateTime") Date createDate);
	
	@Modifying
	@Query("delete from SmsLM s where s.createDate <= :createDateTime")
	void deleteAllWithCreateDateBefore(@Param("createDateTime") Date createDate);
}
