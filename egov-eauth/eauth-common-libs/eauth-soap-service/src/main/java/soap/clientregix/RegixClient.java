package soap.clientregix;

import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.AddressingFeature;

import com.sun.xml.ws.developer.JAXWSProperties;

import soap.clientregix.ServiceRequestData.Argument;
import soap.clientregix.requests.Operation;

/**
 * A client for Regix operations. Consumers can request data from registers through regix by using an instance of this
 * class.
 * 
 * @author bozhanov
 *
 */
public class RegixClient {
    private IRegiXEntryPoint regix;

    /**
     * Factory method for creating a RegixClient
     * @return an initialized regix client
     */
    public static RegixClient create(InputStream keyStore, char[] keyStorePassword) {
        RegixClient client = new RegixClient(new RegiXEntryPoint().getWSHttpBindingIRegiXEntryPoint(new AddressingFeature()));
        client.initTLS(keyStore, keyStorePassword);
        return client;
    }

    public static ServiceRequestData createRequestData(Operation operation, Object requestBody) {
        ServiceRequestData request = new ServiceRequestData();
        request.setOperation(operation.getKey());
        Argument arg = new Argument();
        arg.setAny(requestBody);
        request.setArgument(arg);
        return request;
    }
    
    public RegixClient(IRegiXEntryPoint regix) {
        this.regix = regix;
    }
    
    public void initTLS(InputStream keyStore, char[] keyStorePassword) {
        BindingProvider bindingProvider = (BindingProvider) regix; 
        bindingProvider.getRequestContext().put(JAXWSProperties.SSL_SOCKET_FACTORY, createSocketFactory(keyStore, keyStorePassword));
        
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                //TODO skip the validation only for the RegiX endpoint, and only until it doesn't have a proper domain
                return true;
            }
        };
        
        bindingProvider.getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, allHostsValid);
    }
    
    /**
     * Executes a request and returns the result synchronously
     * @param request
     * @return
     */
    public ServiceResultData executeSynchronous(ServiceRequestData request) {
        return regix.executeSynchronous(request);
    }
    
    /**
     * Sends a request and returns metadata about the request which can then be used with the fetchResult method
     * to get the actual response from the call
     * 
     * @param request
     * @return result
     */
    public ServiceExecuteResult execute(ServiceRequestData request) {
        return regix.execute(request);
    }
    
    /**
     * Fetches the result for a previously called execute() method
     * 
     * @param serviceCallId
     * @return result
     */
    public ServiceResultData fetchResult(BigDecimal serviceCallId) {
        ServiceCheckResultArgument arg = new ServiceCheckResultArgument();
        arg.setServiceCallID(serviceCallId);
        return regix.checkResult(arg);
    }

    private SSLSocketFactory createSocketFactory(InputStream keyStoreStream, char[] keyStorePassword) {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keyStoreStream, keyStorePassword);
            
            KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmFactory.init(keyStore, keyStorePassword);
    
            Enumeration<String> aliases = keyStore.aliases();
            List<X509Certificate> trustedIssuers = new ArrayList<>();
            while (aliases.hasMoreElements()) {
                trustedIssuers.add((X509Certificate) keyStore.getCertificate(aliases.nextElement()));
            }
            X509Certificate[] acceptedIssuers = trustedIssuers.toArray(new X509Certificate[0]);
            
            TrustManager[] trustManagers = new TrustManager[] {
              new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }
                
                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                }
    
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return acceptedIssuers;
                }
              }      
            };
            
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(kmFactory.getKeyManagers(), trustManagers, new java.security.SecureRandom());
            return sc.getSocketFactory();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
