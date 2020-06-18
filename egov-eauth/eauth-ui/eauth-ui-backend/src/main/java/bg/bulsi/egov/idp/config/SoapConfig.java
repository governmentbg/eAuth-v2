package bg.bulsi.egov.idp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import soap.connection.SOAPConnector;

@Configuration
public class SoapConfig {

	@Value("${egov.eauth.sys.int.soap.regres.service.url}")
	private String serviceUrl;
	
	@Value("${egov.eauth.sys.int.soap.regres.service.name}")
	private String methodName;
	
	@Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // package name specified in the <generatePackage> in pom.xml
        marshaller.setContextPath("soap.client");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        String endpoint = serviceUrl + methodName;
        SOAPConnector client = new SOAPConnector();
        client.setDefaultUri(endpoint);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
