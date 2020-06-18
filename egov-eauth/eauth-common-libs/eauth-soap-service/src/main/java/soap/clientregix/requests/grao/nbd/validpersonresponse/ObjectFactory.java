
package soap.clientregix.requests.grao.nbd.validpersonresponse;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the bg.egov.regix.grao.nbd.validpersonresponse package. 
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

    private final static QName _ValidPersonResponse_QNAME = new QName("http://egov.bg/RegiX/GRAO/NBD/ValidPersonResponse", "ValidPersonResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: bg.egov.regix.grao.nbd.validpersonresponse
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidPersonResponseType }
     * 
     */
    public ValidPersonResponseType createValidPersonResponseType() {
        return new ValidPersonResponseType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidPersonResponseType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://egov.bg/RegiX/GRAO/NBD/ValidPersonResponse", name = "ValidPersonResponse")
    public JAXBElement<ValidPersonResponseType> createValidPersonResponse(ValidPersonResponseType value) {
        return new JAXBElement<ValidPersonResponseType>(_ValidPersonResponse_QNAME, ValidPersonResponseType.class, null, value);
    }

}
