package bg.bulsi.egov.idp.client;


import static org.testng.Assert.assertEquals;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import bg.bulsi.egov.idp.IdpApplication;
import bg.bulsi.egov.idp.config.TfaLoginConfig;

@Test(groups = {"pause"})
@SpringBootTest(classes = IdpApplication.class)
public class UserTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TfaLoginConfig loginConfig;
	
	@Test(groups = {"clientTest","pause"})
	private void testConfig() {
		
		assertEquals(loginConfig.getIdentifiers().get(0), "1010101010");
	
	}

}
