package soap;

import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.soap.security.support.KeyManagersFactoryBean;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.soap.security.support.TrustManagersFactoryBean;
import org.springframework.ws.transport.http.HttpsUrlConnectionMessageSender;

import soap.clientregix.CallContext;
import soap.clientregix.RegixClient;
import soap.clientregix.ServiceRequestData;
import soap.clientregix.ServiceRequestData.Argument;
import soap.clientregix.ServiceResultData;
import soap.clientregix.requests.av.TROperation;
import soap.clientregix.requests.av.tr.SearchParticipationInCompaniesRequestType;
import soap.clientregix.requests.av.tr.SearchParticipationInCompaniesResponseType;
import soap.clientregix.requests.grao.GraoOperation;
import soap.clientregix.requests.grao.nbd.validpersonrequest.ValidPersonRequestType;
import soap.clientregix.requests.grao.nbd.validpersonresponse.ValidPersonResponseType;
import soap.connection.BasicAuthHttpsConnectionMessageSender;
import soap.connection.SOAPConnector;

public class SoapConnectorRegixTest {

    /**
     * DB properties :TODO put this in db
     */
    private String ENDPOINT_SOAP_SERVICE = "https://172.23.105.77/RegiX/RegiXEntryPoint.svc";

    private String keystorePath = "keystore.jks";
    private String truststorePath = "truststore.jks";
    private String keystorePassword = "";
    private String truststorePassword = "";
    
    /*
     * BasiAuth
     */
    private String username = "";
    private String password = "";

	private CallContext callContext;
    
    /*
     * CallContext test data
     */
    @Before
	private void init() {
    	CallContext ctx = new CallContext();
    	ctx.setServiceURI("132-11123-01.01.2016");
    	ctx.setServiceType("За проверовъчна дейност");
    	ctx.setEmployeeIdentifier("tl_ytoteva");
    	ctx.setEmployeeNames("tl_ytoteva");
    	ctx.setEmployeeAditionalIdentifier("");
    	ctx.setAdministrationOId("2.16.100.1.1.1.1.15");
    	ctx.setEmployeePosition("SW Developer");
    	ctx.setAdministrationName("Министерство на транспорта, информационните технологии и съобщенията");
    	ctx.setLawReason("За целите на разработката и тестването на RegiX.");
    	this.callContext = ctx;
	}
    
    
    
    @Bean
    public HttpsUrlConnectionMessageSender messageSender() throws Exception {
      HttpsUrlConnectionMessageSender messageSender = new BasicAuthHttpsConnectionMessageSender(username, password);
      messageSender.setTrustManagers(trustManagersFactoryBean().getObject());
      messageSender.setKeyManagers(keyManagersFactoryBean().getObject());
      return messageSender;
    }

    @Bean
    public TrustManagersFactoryBean trustManagersFactoryBean() {
      TrustManagersFactoryBean trustManagersFactoryBean = new TrustManagersFactoryBean();
      trustManagersFactoryBean.setKeyStore(trustStore().getObject());
      return trustManagersFactoryBean;
    }

    @Bean
    public KeyManagersFactoryBean keyManagersFactoryBean() {
      KeyManagersFactoryBean keyManagersFactoryBean = new KeyManagersFactoryBean();
      keyManagersFactoryBean.setKeyStore(keyStore().getObject());
      keyManagersFactoryBean.setPassword(keystorePassword);
      return keyManagersFactoryBean;
    }

    @Bean
    public KeyStoreFactoryBean trustStore() {
      KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
      keyStoreFactoryBean.setLocation(new ClassPathResource(truststorePath)); // Located in src/main/resources
      keyStoreFactoryBean.setPassword(truststorePassword);
      return keyStoreFactoryBean;
    }

    @Bean
    public KeyStoreFactoryBean keyStore() {
      KeyStoreFactoryBean keyStoreFactoryBean = new KeyStoreFactoryBean();
      keyStoreFactoryBean.setLocation(new ClassPathResource(keystorePath));
      keyStoreFactoryBean.setPassword(keystorePassword);
      return keyStoreFactoryBean;
    }
    

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setContextPath("soap.clientregix");
        marshaller.setPackagesToScan("soap.clientregix");
        return marshaller;
    }

    @Bean
    public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
        SOAPConnector client = new SOAPConnector();
        client.setDefaultUri(ENDPOINT_SOAP_SERVICE);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    //@Test
    public void soapConnectorAVRTTest() {

        //connector
        SOAPConnector soapConnector =   soapConnector(marshaller());

        SearchParticipationInCompaniesRequestType requestBody = new SearchParticipationInCompaniesRequestType();
        requestBody.setEGN("1010101010");

    	ServiceRequestData request = new ServiceRequestData();
    	request.setOperation(TROperation.PERSON_IN_COMPANIES_SEARCH.getKey());
	    
    	Argument arg = new Argument();
        arg.setAny(requestBody);
        request.setArgument(arg);
    	
		CallContext ctx = new CallContext();
//		ctx.setAdministrationName("Министерски съвет");
//		ctx.setEmployeeIdentifier("12345");
	
		request.setCallContext(ctx);
        
        ServiceResultData response;
        SearchParticipationInCompaniesResponseType responseBody;
        try {
			response =(ServiceResultData) soapConnector.callWebService(ENDPOINT_SOAP_SERVICE, request, messageSender());
			responseBody = (SearchParticipationInCompaniesResponseType) response.getData().getResponse().getAny();
			
			System.out.println("Got Response As below PERSON_IN_COMPANIES_SEARCH  ========= : ");

	        responseBody.getCompanyParticipation().getCompany().stream().forEach(r -> {
	            System.out.println(r.getEIK() + " " + r.getCompanyName());
	        });
			
		} catch (Exception e) {
			System.out.println("Got ERROR As below PERSON_IN_COMPANIES_SEARCH  ========= : ");
			e.printStackTrace();
		}

    }
    
    //@Test
    public void soapConnectorGRAOTest() {

        SOAPConnector soapConnector =   soapConnector(marshaller());

        ValidPersonRequestType requestBody = new ValidPersonRequestType();
        requestBody.setEGN("1010101010");

        ServiceRequestData request = new ServiceRequestData();
    	request.setOperation(GraoOperation.VALID_PERSON_SEARCH.getKey());
	    
    	Argument arg = new Argument();
        arg.setAny(requestBody);
        request.setArgument(arg);
    	
		CallContext ctx = new CallContext();
//		ctx.setAdministrationName("Министерски съвет");
//		ctx.setEmployeeIdentifier("12345");
	
		request.setCallContext(ctx);
        
        ServiceResultData response;
        ValidPersonResponseType responseBody;
        try {
			response =(ServiceResultData) soapConnector.callWebService(ENDPOINT_SOAP_SERVICE, request, messageSender());
			responseBody = (ValidPersonResponseType) response.getData().getResponse().getAny();
			
			System.out.println("Got Response As below VALID_PERSON_SEARCH  ========= : ");

	        System.out.println(responseBody.getDeathDate());
			
		} catch (Exception e) {
			System.out.println("Got ERROR As below VALID_PERSON_SEARCH  ========= : ");
			e.printStackTrace();
		}
        
    }

    /*
     * Regix client example
     */
    //@Test
    public void regixTest() {
    	FileInputStream keyStoreStream = null;
		try {
			keyStoreStream = new FileInputStream(keystorePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	RegixClient client = RegixClient.create(keyStoreStream, keystorePassword.toCharArray());

        ValidPersonRequestType requestBody = new ValidPersonRequestType();
        requestBody.setEGN("1010101010");

    	ServiceRequestData requestData = RegixClient.createRequestData(GraoOperation.VALID_PERSON_SEARCH, requestBody);
    	
    	requestData.setCallContext(callContext);
    	requestData.setSignResult(true);
    	requestData.setReturnAccessMatrix(true);
    	
    	ServiceResultData serviceResultData = client.executeSynchronous(requestData);
    	ValidPersonResponseType validPersonResponseType = (ValidPersonResponseType) serviceResultData.getData().getResponse().getAny();
    	
    	System.out.println(validPersonResponseType.getBirthDate());
    	
    	assertTrue(validPersonResponseType.getDeathDate() == null);
    	
    }

}
