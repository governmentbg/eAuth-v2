package soap.clientregix;

import java.lang.reflect.Field;
import java.security.Security;

import soap.clientregix.requests.grao.GraoOperation;
import soap.clientregix.requests.grao.pna.PermanentAddressRequest;

public class RegixClientDemo {

    public static void main(String[] args) throws Exception {
        
        String keystorePassword = args[0];
        
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        
        PermanentAddressRequest personDataRequest = new PermanentAddressRequest();
        personDataRequest.setEGN("test");
        
        RegixClient client = RegixClient.create(RegixClientDemo.class.getResourceAsStream("/test.jks"), keystorePassword.toCharArray());
        ServiceRequestData requestData = RegixClient.createRequestData(GraoOperation.PERMANENT_ADDRESS_SEARCH, personDataRequest);
        
        CallContext ctx = new CallContext();
        requestData.setCallContext(ctx);
        
        System.out.println(client.executeSynchronous(requestData).getData().getResponse().getAny());
    }

    @SuppressWarnings("unused")
    private static void setupTLS() throws Exception {
        // strong security is enabled by default for Java 12

        // enable strong security BEFORE Java 12
        Security.setProperty("crypto.policy", "unlimited");

        // enable strong security BEFORE Java 9
        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
        field.setAccessible(true);
        field.set(null, java.lang.Boolean.FALSE);
    }
}
