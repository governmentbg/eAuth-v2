package soap.clientregix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CallContext complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CallContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceURI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ServiceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmployeeIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmployeeNames" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmployeeAditionalIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EmployeePosition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdministrationOId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AdministrationName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ResponsiblePersonIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LawReason" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CallContext", propOrder = {
    "serviceURI",
    "serviceType",
    "employeeIdentifier",
    "employeeNames",
    "employeeAditionalIdentifier",
    "employeePosition",
    "administrationOId",
    "administrationName",
    "responsiblePersonIdentifier",
    "lawReason",
    "remark"
})
public class CallContext {

    @XmlElement(name = "ServiceURI")
    protected String serviceURI;
    @XmlElement(name = "ServiceType")
    protected String serviceType;
    @XmlElement(name = "EmployeeIdentifier")
    protected String employeeIdentifier;
    @XmlElement(name = "EmployeeNames")
    protected String employeeNames;
    @XmlElement(name = "EmployeeAditionalIdentifier")
    protected String employeeAditionalIdentifier;
    @XmlElement(name = "EmployeePosition")
    protected String employeePosition;
    @XmlElement(name = "AdministrationOId")
    protected String administrationOId;
    @XmlElement(name = "AdministrationName")
    protected String administrationName;
    @XmlElement(name = "ResponsiblePersonIdentifier")
    protected String responsiblePersonIdentifier;
    @XmlElement(name = "LawReason")
    protected String lawReason;
    @XmlElement(name = "Remark")
    protected String remark;

    /**
     * Gets the value of the serviceURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceURI() {
        return serviceURI;
    }

    /**
     * Sets the value of the serviceURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceURI(String value) {
        this.serviceURI = value;
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
     * Gets the value of the employeeIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeIdentifier() {
        return employeeIdentifier;
    }

    /**
     * Sets the value of the employeeIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeIdentifier(String value) {
        this.employeeIdentifier = value;
    }

    /**
     * Gets the value of the employeeNames property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeNames() {
        return employeeNames;
    }

    /**
     * Sets the value of the employeeNames property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeNames(String value) {
        this.employeeNames = value;
    }

    /**
     * Gets the value of the employeeAditionalIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeeAditionalIdentifier() {
        return employeeAditionalIdentifier;
    }

    /**
     * Sets the value of the employeeAditionalIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeeAditionalIdentifier(String value) {
        this.employeeAditionalIdentifier = value;
    }

    /**
     * Gets the value of the employeePosition property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmployeePosition() {
        return employeePosition;
    }

    /**
     * Sets the value of the employeePosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmployeePosition(String value) {
        this.employeePosition = value;
    }

    /**
     * Gets the value of the administrationOId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdministrationOId() {
        return administrationOId;
    }

    /**
     * Sets the value of the administrationOId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdministrationOId(String value) {
        this.administrationOId = value;
    }

    /**
     * Gets the value of the administrationName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdministrationName() {
        return administrationName;
    }

    /**
     * Sets the value of the administrationName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdministrationName(String value) {
        this.administrationName = value;
    }

    /**
     * Gets the value of the responsiblePersonIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsiblePersonIdentifier() {
        return responsiblePersonIdentifier;
    }

    /**
     * Sets the value of the responsiblePersonIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsiblePersonIdentifier(String value) {
        this.responsiblePersonIdentifier = value;
    }

    /**
     * Gets the value of the lawReason property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLawReason() {
        return lawReason;
    }

    /**
     * Sets the value of the lawReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLawReason(String value) {
        this.lawReason = value;
    }

    /**
     * Gets the value of the remark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Sets the value of the remark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark(String value) {
        this.remark = value;
    }

}
