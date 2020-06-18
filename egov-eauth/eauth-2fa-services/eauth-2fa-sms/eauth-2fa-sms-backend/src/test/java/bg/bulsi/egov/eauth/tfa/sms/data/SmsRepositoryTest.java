package bg.bulsi.egov.eauth.tfa.sms.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import bg.bulsi.egov.eauth.tfa.sms.model.Sms;

@SpringBootTest
public class SmsRepositoryTest {

	private static SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private SmsRepository repository;

	@Test
	public void test() throws ParseException {
		String timestamp = "2019-11-25 18:00:00";
		List<Sms> list = repository.findAllWithCreateDateBefore(sdfTimestamp.parse(timestamp));
		int size = list.size();
		assertEquals(size, size);
	}
}
