//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.16 at 11:22:01 AM EEST 
//


package bg.bulsi.egov.saml.schema;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bg.bulsi.egov.saml.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Claim_QNAME = new QName("urn:bg:egov:eauth:2.0:saml:ext", "Claim");
    private final static QName _SPContext_QNAME = new QName("urn:bg:egov:eauth:2.0:saml:ext", "SPContext");
    private final static QName _RequestedService_QNAME = new QName("urn:bg:egov:eauth:2.0:saml:ext", "RequestedService");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bg.bulsi.egov.saml.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RequestedServiceType }
     * 
     */
    public RequestedServiceType createRequestedServiceType() {
        return new RequestedServiceType();
    }

    /**
     * Create an instance of {@link ClaimType }
     * 
     */
    public ClaimType createClaimType() {
        return new ClaimType();
    }

    /**
     * Create an instance of {@link SPContextType }
     * 
     */
    public SPContextType createSPContextType() {
        return new SPContextType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClaimType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:bg:egov:eauth:2.0:saml:ext", name = "Claim")
    public JAXBElement<ClaimType> createClaim(ClaimType value) {
        return new JAXBElement<ClaimType>(_Claim_QNAME, ClaimType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SPContextType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:bg:egov:eauth:2.0:saml:ext", name = "SPContext")
    public JAXBElement<SPContextType> createSPContext(SPContextType value) {
        return new JAXBElement<SPContextType>(_SPContext_QNAME, SPContextType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RequestedServiceType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:bg:egov:eauth:2.0:saml:ext", name = "RequestedService")
    public JAXBElement<RequestedServiceType> createRequestedService(RequestedServiceType value) {
        return new JAXBElement<RequestedServiceType>(_RequestedService_QNAME, RequestedServiceType.class, null, value);
    }

}
