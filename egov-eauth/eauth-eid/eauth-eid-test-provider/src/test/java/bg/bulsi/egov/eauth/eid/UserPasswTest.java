package bg.bulsi.egov.eauth.eid;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.eid.provider.MainApplication;
import bg.bulsi.egov.eauth.eid.provider.model.Identity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test
@SpringBootTest(classes = MainApplication.class)
public class UserPasswTest extends AbstractTestNGSpringContextTests {

	@Autowired
	PasswordEncoder passwordEncoder;

	@Test(groups = {"userPasswTest"})
	public void testPassw() {
		
		Identity identity = new Identity();
		
		String passw = "admin";
		/*
		 * BCript SHA-1 ???
		 */
		String hashPassw = passwordEncoder.encode(passw);

//		identity.setId(id);
		identity.setNid("1010101011");
		identity.setNames("Names");
		identity.setUsername("newadmin");
		identity.setPassword(hashPassw);
		identity.setPhone("555555555");
		identity.setCreateDate(new Date());
		identity.setEditDate(new Date());
		identity.setActive(true);
		
		log.info("HASHpassw: {}", hashPassw);
			
	}

}
