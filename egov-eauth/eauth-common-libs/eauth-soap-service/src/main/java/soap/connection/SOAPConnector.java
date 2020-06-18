package soap.connection;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.WebServiceMessageSender;

public class SOAPConnector extends WebServiceGatewaySupport {

    public Object callWebService(String url, Object request){
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }
    
    public Object callWebService(String url, Object request, WebServiceMessageSender messageSender) {
    	getWebServiceTemplate().setMessageSender(messageSender);
        return getWebServiceTemplate().marshalSendAndReceive(url, request);
    }
}