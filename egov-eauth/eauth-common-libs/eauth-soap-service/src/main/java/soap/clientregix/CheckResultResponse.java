
package soap.clientregix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="CheckResultResult" type="{http://tempuri.org/}ServiceResultData" minOccurs="0"/>
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
    "checkResultResult"
})
@XmlRootElement(name = "CheckResultResponse")
public class CheckResultResponse {

    @XmlElement(name = "CheckResultResult")
    protected ServiceResultData checkResultResult;

    /**
     * Gets the value of the checkResultResult property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceResultData }
     *     
     */
    public ServiceResultData getCheckResultResult() {
        return checkResultResult;
    }

    /**
     * Sets the value of the checkResultResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceResultData }
     *     
     */
    public void setCheckResultResult(ServiceResultData value) {
        this.checkResultResult = value;
    }

}
