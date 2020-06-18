package bg.bulsi.egov.eauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class Eauth2faSmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Eauth2faSmsApplication.class, args);
	}

}
