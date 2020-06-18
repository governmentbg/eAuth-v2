package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * IdentityAttributes
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:59:49.722+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "IdentityAttributes")
@XmlRootElement(name = "IdentityAttributes")
@XmlAccessorType(XmlAccessType.FIELD)public class IdentityAttributes  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("oid")
  @JacksonXmlProperty(localName = "oid")
  private String oid = null;

  @JsonProperty("urn")
  @JacksonXmlProperty(localName = "urn")
  private String urn = null;

  @JsonProperty("value")
  @JacksonXmlProperty(localName = "value")
  private String value = null;

  public IdentityAttributes oid(String oid) {
    this.oid = oid;
    return this;
  }

  /**
   * Get oid
   * @return oid
  **/
  @ApiModelProperty(value = "")
  
    public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public IdentityAttributes urn(String urn) {
    this.urn = urn;
    return this;
  }

  /**
   * Get urn
   * @return urn
  **/
  @ApiModelProperty(value = "")
  
    public String getUrn() {
    return urn;
  }

  public void setUrn(String urn) {
    this.urn = urn;
  }

  public IdentityAttributes value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  **/
  @ApiModelProperty(value = "")
  
    public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdentityAttributes identityAttributes = (IdentityAttributes) o;
    return Objects.equals(this.oid, identityAttributes.oid) &&
        Objects.equals(this.urn, identityAttributes.urn) &&
        Objects.equals(this.value, identityAttributes.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(oid, urn, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class IdentityAttributes {\n");
    
    sb.append("    oid: ").append(toIndentedString(oid)).append("\n");
    sb.append("    urn: ").append(toIndentedString(urn)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
