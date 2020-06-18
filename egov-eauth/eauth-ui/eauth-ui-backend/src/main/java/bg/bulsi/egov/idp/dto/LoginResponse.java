package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * Principal of succesfuly authenticated user
 */
@ApiModel(description = "Principal of succesfuly authenticated user")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:59:49.722+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "LoginResponse")
@XmlRootElement(name = "LoginResponse")
@XmlAccessorType(XmlAccessType.FIELD)public class LoginResponse  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("inResponseTid")
  @JacksonXmlProperty(localName = "inResponseTid")
  private String inResponseTid = null;

  @JsonProperty("relayState")
  @JacksonXmlProperty(localName = "relayState")
  private String relayState = null;

  @JsonProperty("loa")
  @JacksonXmlProperty(localName = "loa")
  private LevelOfAssurance loa = null;

  @JsonProperty("providerId")
  @JacksonXmlProperty(localName = "providerId")
  private String providerId = null;

  @JsonProperty("attributes")
  @JacksonXmlProperty(localName = "attributes")
  @Valid
  private List<IdentityAttributes> attributes = null;

  public LoginResponse inResponseTid(String inResponseTid) {
    this.inResponseTid = inResponseTid;
    return this;
  }

  /**
   * Get inResponseTid
   * @return inResponseTid
  **/
  @ApiModelProperty(value = "")
  
    public String getInResponseTid() {
    return inResponseTid;
  }

  public void setInResponseTid(String inResponseTid) {
    this.inResponseTid = inResponseTid;
  }

  public LoginResponse relayState(String relayState) {
    this.relayState = relayState;
    return this;
  }

  /**
   * Get relayState
   * @return relayState
  **/
  @ApiModelProperty(value = "")
  
    public String getRelayState() {
    return relayState;
  }

  public void setRelayState(String relayState) {
    this.relayState = relayState;
  }

  public LoginResponse loa(LevelOfAssurance loa) {
    this.loa = loa;
    return this;
  }

  /**
   * Get loa
   * @return loa
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public LevelOfAssurance getLoa() {
    return loa;
  }

  public void setLoa(LevelOfAssurance loa) {
    this.loa = loa;
  }

  public LoginResponse providerId(String providerId) {
    this.providerId = providerId;
    return this;
  }

  /**
   * Get providerId
   * @return providerId
  **/
  @ApiModelProperty(value = "")
  
    public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public LoginResponse attributes(List<IdentityAttributes> attributes) {
    this.attributes = attributes;
    return this;
  }

  public LoginResponse addAttributesItem(IdentityAttributes attributesItem) {
    if (this.attributes == null) {
      this.attributes = new ArrayList<>();
    }
    this.attributes.add(attributesItem);
    return this;
  }

  /**
   * Get attributes
   * @return attributes
  **/
  @ApiModelProperty(value = "")
      @Valid
    public List<IdentityAttributes> getAttributes() {
    return attributes;
  }

  public void setAttributes(List<IdentityAttributes> attributes) {
    this.attributes = attributes;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginResponse loginResponse = (LoginResponse) o;
    return Objects.equals(this.inResponseTid, loginResponse.inResponseTid) &&
        Objects.equals(this.relayState, loginResponse.relayState) &&
        Objects.equals(this.loa, loginResponse.loa) &&
        Objects.equals(this.providerId, loginResponse.providerId) &&
        Objects.equals(this.attributes, loginResponse.attributes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inResponseTid, relayState, loa, providerId, attributes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginResponse {\n");
    
    sb.append("    inResponseTid: ").append(toIndentedString(inResponseTid)).append("\n");
    sb.append("    relayState: ").append(toIndentedString(relayState)).append("\n");
    sb.append("    loa: ").append(toIndentedString(loa)).append("\n");
    sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
    sb.append("    attributes: ").append(toIndentedString(attributes)).append("\n");
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
