package bg.bulsi.egov.eauth.test.sp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import bg.bulsi.egov.eauth.test.sp.security.saml.SamlConfiguration;

@SpringBootApplication
// @Import(SamlConfiguration.class)
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}
