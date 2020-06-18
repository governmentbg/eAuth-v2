package bg.bulsi.egov.idp.services.validator;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import soap.client.SearchResourceByOID;
import soap.client.SearchResourceByOIDResponse;
import soap.connection.SOAPConnector;

@Slf4j
@Service
public class AuthnRequestValidator {

	@Autowired
	private SOAPConnector soapClient;
	
	
	@Value("${egov.eauth.sys.int.soap.regres.service.url}")
	private String serviceUrl;

	@Value("${egov.eauth.sys.int.soap.regres.service.name}")
	private String methodName;


	public boolean validateResourceOID(String oid) {

		SearchResourceByOID searchResource = new SearchResourceByOID();

		searchResource.getOID().add(oid);

		String endpoint = serviceUrl + methodName;
		SearchResourceByOIDResponse response = (SearchResourceByOIDResponse) soapClient.callWebService(endpoint, searchResource);
		
		log.info("Got Response As below GetResourceInfo.wsdl  ========= : ");
		response.getResource().stream().forEach(r -> {
			log.info("{} {} [{}]", r.getOID(), r.getResourceName(), r.getResourceType().value());

		});

		boolean valid = !response.getResource().isEmpty();
		
		return valid;
	}
	
	public boolean validateExtensionsBySchema() {
		// TODO: use validateXmlByXsd
		return true;
	}
	
	public boolean validateRequestBySchema() {
		// TODO: use validateXmlByXsd
		return true;
	}
	
	private boolean validateXmlByXsd(InputStream xml, InputStream xsd) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(xsd));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xml));
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
