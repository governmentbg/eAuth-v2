
package soap.clientregix.requests.grao.nbd.validpersonrequest;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bg.egov.regix.grao.nbd.validpersonrequest package. 
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

    private final static QName _ValidPersonRequest_QNAME = new QName("http://egov.bg/RegiX/GRAO/NBD/ValidPersonRequest", "ValidPersonRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bg.egov.regix.grao.nbd.validpersonrequest
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidPersonRequestType }
     * 
     */
    public ValidPersonRequestType createValidPersonRequestType() {
        return new ValidPersonRequestType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidPersonRequestType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://egov.bg/RegiX/GRAO/NBD/ValidPersonRequest", name = "ValidPersonRequest")
    public JAXBElement<ValidPersonRequestType> createValidPersonRequest(ValidPersonRequestType value) {
        return new JAXBElement<ValidPersonRequestType>(_ValidPersonRequest_QNAME, ValidPersonRequestType.class, null, value);
    }

}
