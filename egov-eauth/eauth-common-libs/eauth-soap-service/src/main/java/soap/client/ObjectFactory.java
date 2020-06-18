
package soap.client;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the soap.client package. 
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


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: soap.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ResourceInfo }
     * 
     */
    public ResourceInfo createResourceInfo() {
        return new ResourceInfo();
    }

    /**
     * Create an instance of {@link SearchResourceByOID }
     * 
     */
    public SearchResourceByOID createSearchResourceByOID() {
        return new SearchResourceByOID();
    }

    /**
     * Create an instance of {@link SearchResourceByOIDResponse }
     * 
     */
    public SearchResourceByOIDResponse createSearchResourceByOIDResponse() {
        return new SearchResourceByOIDResponse();
    }

    /**
     * Create an instance of {@link AttributeType }
     * 
     */
    public AttributeType createAttributeType() {
        return new AttributeType();
    }

    /**
     * Create an instance of {@link Service }
     * 
     */
    public Service createService() {
        return new Service();
    }

    /**
     * Create an instance of {@link ResourceInfo.Attributes }
     * 
     */
    public ResourceInfo.Attributes createResourceInfoAttributes() {
        return new ResourceInfo.Attributes();
    }

}
