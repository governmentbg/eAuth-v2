package bg.bulsi.egov.idp.services.temp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import soap.client.ResourceInfo;
import soap.client.SearchResourceByOID;
import soap.client.SearchResourceByOIDResponse;
import soap.connection.SOAPConnector;

@Slf4j
@Service
public class ResourceRegistryService {

	@Autowired
	private SOAPConnector soapClient;
	
	
	@Value("${egov.eauth.sys.int.soap.regres.service.url}")
	private String serviceUrl;

	@Value("${egov.eauth.sys.int.soap.regres.service.name}")
	private String methodName;

	public String findNameByOID(String oid) {

		String resourceName = null;
		
		SearchResourceByOID searchResource = new SearchResourceByOID();

		searchResource.getOID().add(oid);

		String endpoint = serviceUrl + methodName;
		SearchResourceByOIDResponse response = (SearchResourceByOIDResponse) soapClient.callWebService(endpoint, searchResource);
		
		Optional<ResourceInfo> findFirst = response.getResource().stream().findFirst();

		if (findFirst.isPresent()) {
			resourceName = findFirst.get().getResourceName();
		}
		
		log.info("For Service with OID: [{}] found Resource Name: [{}]", oid, resourceName);
		
		return resourceName;
	}
}
