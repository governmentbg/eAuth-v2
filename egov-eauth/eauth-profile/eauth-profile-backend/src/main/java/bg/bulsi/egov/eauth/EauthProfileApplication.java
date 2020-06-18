package bg.bulsi.egov.eauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@SpringBootApplication()
//scanBasePackages =  {"bg.bulsi.egov.eauth.repository", "bg.bulsi.egov.eauth.profile.rest"}
//@ComponentScan(basePackages= {"bg.bulsi.egov.eauth.repository","bg.bulsi.egov.eauth.services.profile"})
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class})
public class EauthProfileApplication {

	public static void main(String[] args) {
		SpringApplication.run(EauthProfileApplication.class, args);
	}

}
