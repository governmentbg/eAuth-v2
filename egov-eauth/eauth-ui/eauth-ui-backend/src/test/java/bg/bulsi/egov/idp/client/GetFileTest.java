package bg.bulsi.egov.idp.client;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import bg.bulsi.egov.idp.IdpApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Test(groups = { "" })
@SpringBootTest(classes = IdpApplication.class)
public class GetFileTest extends AbstractTestNGSpringContextTests {

	@Value("${server.ssl.trust-store}")
	String trustStorePath;


	@Test(groups = { "fileTest","brokenTest" })
	private void testGFile() {

		// @Value("${server.ssl.trust-store-password}")
		// private String trustStorePass;
		//
		// @Value("${server.ssl.trust-store-type}")null
		// private String trustStoreType;

		File trustStoreFile = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource(trustStorePath);
		try {
			log.info("File Found NotException ### trust store");
			trustStoreFile = new File(url.toURI());
		} catch (URISyntaxException e) {
			log.info("FileNotFoundException ### trust store");
			e.printStackTrace();
		}

		assert (trustStoreFile.exists());
	}

}
