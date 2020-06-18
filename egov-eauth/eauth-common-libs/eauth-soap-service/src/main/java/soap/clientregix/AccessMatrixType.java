package soap.clientregix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccessMatrixType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccessMatrixType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HasAccess" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Properties" type="{http://tempuri.org/}ArrayOfAMPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccessMatrixType", propOrder = {
    "hasAccess",
    "name",
    "properties"
})
@XmlSeeAlso({
    soap.clientregix.DataContainer.Matrix.class
})
public class AccessMatrixType {

    @XmlElement(name = "HasAccess")
    protected boolean hasAccess;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Properties")
    protected ArrayOfAMPropertyType properties;

    /**
     * Gets the value of the hasAccess property.
     * 
     */
    public boolean isHasAccess() {
        return hasAccess;
    }

    /**
     * Sets the value of the hasAccess property.
     * 
     */
    public void setHasAccess(boolean value) {
        this.hasAccess = value;
    }

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
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAMPropertyType }
     *     
     */
    public ArrayOfAMPropertyType getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAMPropertyType }
     *     
     */
    public void setProperties(ArrayOfAMPropertyType value) {
        this.properties = value;
    }

}
