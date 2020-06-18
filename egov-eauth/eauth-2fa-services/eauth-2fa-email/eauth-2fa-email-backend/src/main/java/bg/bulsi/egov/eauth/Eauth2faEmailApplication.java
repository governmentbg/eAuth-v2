package bg.bulsi.egov.eauth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Eauth2faEmailApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Eauth2faEmailApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
