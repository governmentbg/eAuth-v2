package bg.bulsi.egov.eauth;

import bg.bulsi.egov.eauth.tfa.totp.service.totp.TotpPasswordGeneratorService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URISyntaxException;

import javax.transaction.Transactional;

@SpringBootTest
class Eauth2faTotpApplicationTests {

	@Autowired
	TotpPasswordGeneratorService totpPasswordGeneratorService;

	//@Test
	@Transactional
	void contextLoads() throws URISyntaxException {


		String secretKey = totpPasswordGeneratorService.generate("3010101010", "auth").getSecret();

		GoogleAuthenticator gAuth = new GoogleAuthenticator();
		int code = gAuth.getTotpPassword(secretKey);



	 	boolean ok =	totpPasswordGeneratorService.verify("3010101010", String.valueOf(code));

	 	assert ok;
	}

}
