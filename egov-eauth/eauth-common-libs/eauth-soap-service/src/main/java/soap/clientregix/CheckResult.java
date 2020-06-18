
package soap.clientregix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="argument" type="{http://tempuri.org/}ServiceCheckResultArgument" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "argument"
})
@XmlRootElement(name = "CheckResult")
public class CheckResult {

    protected ServiceCheckResultArgument argument;

    /**
     * Gets the value of the argument property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCheckResultArgument }
     *     
     */
    public ServiceCheckResultArgument getArgument() {
        return argument;
    }

    /**
     * Sets the value of the argument property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCheckResultArgument }
     *     
     */
    public void setArgument(ServiceCheckResultArgument value) {
        this.argument = value;
    }

}
