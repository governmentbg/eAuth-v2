package soap;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import soap.connection.SOAPConnector;
import soap.client.*;

public class SoapConnectorTest {


    /**
     * Test oids
     */
    private final String OID_INFORMATIO_SYSTEM = "2.16.100.1.1.8.1";
    private final String OID_SERVICE = "2.16.100.1.1.8.1.1";
    private final String OID_SERVICE_VERSION = "2.16.100.1.1.8.1.1.1";

    /**
     * DB properties :TODO put this in db
     */
    private String ENDPOINT_SOAP_SERVICE="http://172.23.107.76:8080/resreg/";
    private String SOAP_SERVICE_NAME="GetResourceInfo";

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // this is the package name specified in the <generatePackage> specified in
        // pom.xml
        marshaller.setContextPath("soap.client");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        String url = ENDPOINT_SOAP_SERVICE + SOAP_SERVICE_NAME;
        SOAPConnector client = new SOAPConnector();
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Test
    public void soapConnectorTest() {

        //connector
        SOAPConnector soapConnector =   soapConnector(marshaller() );


        SearchResourceByOID  request = new SearchResourceByOID();

        //oid of information system
        request.getOID().add(OID_INFORMATIO_SYSTEM);
        //oid of service
        request.getOID().add(OID_SERVICE);
        //oid of service version
        request.getOID().add(OID_SERVICE_VERSION);

        SearchResourceByOIDResponse response =(SearchResourceByOIDResponse) soapConnector.callWebService("http://172.23.107.76:8080/resreg/GetResourceInfo", request);
        System.out.println("Got Response As below GetResourceInfo.wsdl  ========= : ");

        response.getResource().stream().forEach(r -> {

            System.out.println(r.getOID() + " " + r.getResourceName() + " ( " + r.getResourceType().value() + " ) ");

        });
        
    }

}
