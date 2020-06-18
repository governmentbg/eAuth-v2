
package soap.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Service complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Service"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="oid" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="authenticationMethod" type="{http://egov.bg/regres/domain/v1}AuthenticationMethodsEnum"/&gt;
 *         &lt;element name="isPersonalDataAccess" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="serviceType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="protocolType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="accessPoint" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="environmentType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SUNAU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service", propOrder = {
    "name",
    "oid",
    "authenticationMethod",
    "isPersonalDataAccess",
    "description",
    "serviceType",
    "protocolType",
    "accessPoint",
    "environmentType",
    "sunau"
})
public class Service {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String oid;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AuthenticationMethodsEnum authenticationMethod;
    protected boolean isPersonalDataAccess;
    protected String description;
    @XmlElement(required = true)
    protected String serviceType;
    @XmlElement(required = true)
    protected String protocolType;
    @XmlElement(required = true)
    protected String accessPoint;
    @XmlElement(required = true)
    protected String environmentType;
    @XmlElement(name = "SUNAU")
    protected String sunau;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the authenticationMethod property.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationMethodsEnum }
     *     
     */
    public AuthenticationMethodsEnum getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * Sets the value of the authenticationMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationMethodsEnum }
     *     
     */
    public void setAuthenticationMethod(AuthenticationMethodsEnum value) {
        this.authenticationMethod = value;
    }

    /**
     * Gets the value of the isPersonalDataAccess property.
     * 
     */
    public boolean isIsPersonalDataAccess() {
        return isPersonalDataAccess;
    }

    /**
     * Sets the value of the isPersonalDataAccess property.
     * 
     */
    public void setIsPersonalDataAccess(boolean value) {
        this.isPersonalDataAccess = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceType(String value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the protocolType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocolType() {
        return protocolType;
    }

    /**
     * Sets the value of the protocolType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocolType(String value) {
        this.protocolType = value;
    }

    /**
     * Gets the value of the accessPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccessPoint() {
        return accessPoint;
    }

    /**
     * Sets the value of the accessPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccessPoint(String value) {
        this.accessPoint = value;
    }

    /**
     * Gets the value of the environmentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEnvironmentType() {
        return environmentType;
    }

    /**
     * Sets the value of the environmentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEnvironmentType(String value) {
        this.environmentType = value;
    }

    /**
     * Gets the value of the sunau property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUNAU() {
        return sunau;
    }

    /**
     * Sets the value of the sunau property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUNAU(String value) {
        this.sunau = value;
    }

}
