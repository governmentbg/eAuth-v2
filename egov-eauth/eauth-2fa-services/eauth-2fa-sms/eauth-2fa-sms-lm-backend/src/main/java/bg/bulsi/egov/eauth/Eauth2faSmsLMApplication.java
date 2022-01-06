package bg.bulsi.egov.eauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Eauth2faSmsLMApplication {

	public static void main(String[] args) {
		SpringApplication.run(Eauth2faSmsLMApplication.class, args);
	}

}
