
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
 *         &lt;element name="ExecuteSynchronousResult" type="{http://tempuri.org/}ServiceResultData" minOccurs="0"/>
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
    "executeSynchronousResult"
})
@XmlRootElement(name = "ExecuteSynchronousResponse")
public class ExecuteSynchronousResponse {

    @XmlElement(name = "ExecuteSynchronousResult")
    protected ServiceResultData executeSynchronousResult;

    /**
     * Gets the value of the executeSynchronousResult property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceResultData }
     *     
     */
    public ServiceResultData getExecuteSynchronousResult() {
        return executeSynchronousResult;
    }

    /**
     * Sets the value of the executeSynchronousResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceResultData }
     *     
     */
    public void setExecuteSynchronousResult(ServiceResultData value) {
        this.executeSynchronousResult = value;
    }

}
