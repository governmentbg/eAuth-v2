
package soap.clientregix;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ServiceExecuteResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceExecuteResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceCallID" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="HasError" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceExecuteResult", propOrder = {
    "serviceCallID",
    "hasError",
    "error"
})
public class ServiceExecuteResult {

    @XmlElement(name = "ServiceCallID", required = true)
    protected BigDecimal serviceCallID;
    @XmlElement(name = "HasError")
    protected boolean hasError;
    @XmlElement(name = "Error")
    protected String error;

    /**
     * Gets the value of the serviceCallID property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getServiceCallID() {
        return serviceCallID;
    }

    /**
     * Sets the value of the serviceCallID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setServiceCallID(BigDecimal value) {
        this.serviceCallID = value;
    }

    /**
     * Gets the value of the hasError property.
     * 
     */
    public boolean isHasError() {
        return hasError;
    }

    /**
     * Sets the value of the hasError property.
     * 
     */
    public void setHasError(boolean value) {
        this.hasError = value;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setError(String value) {
        this.error = value;
    }

}
