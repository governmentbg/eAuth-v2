package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * AuthenticationAttribute
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:48:07.018+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "AuthenticationAttribute")
@XmlRootElement(name = "AuthenticationAttribute")
@XmlAccessorType(XmlAccessType.FIELD)public class AuthenticationAttribute  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("id")
  @JacksonXmlProperty(localName = "id")
  private String id = null;

  @JsonProperty("label")
  @JacksonXmlProperty(localName = "label")
  private Label label = null;

  @JsonProperty("mandatory")
  @JacksonXmlProperty(localName = "mandatory")
  private Boolean mandatory = false;

  @JsonProperty("type")
  @JacksonXmlProperty(localName = "type")
  private AttributeType type = null;

  public AuthenticationAttribute id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")
  
    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AuthenticationAttribute label(Label label) {
    this.label = label;
    return this;
  }

  /**
   * Get label
   * @return label
  **/
  @ApiModelProperty(value = "")
  
    public Label getLabel() {
    return label;
  }

  public void setLabel(Label label) {
    this.label = label;
  }

  public AuthenticationAttribute mandatory(Boolean mandatory) {
    this.mandatory = mandatory;
    return this;
  }

  /**
   * Is this particular attribute requred or not
   * @return mandatory
  **/
  @ApiModelProperty(value = "Is this particular attribute requred or not")
  
    public Boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory(Boolean mandatory) {
    this.mandatory = mandatory;
  }

  public AuthenticationAttribute type(AttributeType type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public AttributeType getType() {
    return type;
  }

  public void setType(AttributeType type) {
    this.type = type;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationAttribute authenticationAttribute = (AuthenticationAttribute) o;
    return Objects.equals(this.id, authenticationAttribute.id) &&
        Objects.equals(this.label, authenticationAttribute.label) &&
        Objects.equals(this.mandatory, authenticationAttribute.mandatory) &&
        Objects.equals(this.type, authenticationAttribute.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, label, mandatory, type);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationAttribute {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    label: ").append(toIndentedString(label)).append("\n");
    sb.append("    mandatory: ").append(toIndentedString(mandatory)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
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
