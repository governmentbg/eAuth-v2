package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * LoginRequest
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:59:49.722+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "LoginRequest")
@XmlRootElement(name = "LoginRequest")
@XmlAccessorType(XmlAccessType.FIELD)public class LoginRequest  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("providerId")
  @JacksonXmlProperty(localName = "providerId")
  private String providerId = null;

  @JsonProperty("authMap")
  @JacksonXmlProperty(localName = "authMap")
  private AuthenticationMap authMap = null;

  public LoginRequest providerId(String providerId) {
    this.providerId = providerId;
    return this;
  }

  /**
   * Get providerId
   * @return providerId
  **/
  @ApiModelProperty(required = true, value = "")
      @NotNull

    public String getProviderId() {
    return providerId;
  }

  public void setProviderId(String providerId) {
    this.providerId = providerId;
  }

  public LoginRequest authMap(AuthenticationMap authMap) {
    this.authMap = authMap;
    return this;
  }

  /**
   * Get authMap
   * @return authMap
  **/
  @ApiModelProperty(required = true, value = "")
      @NotNull

    @Valid
    public AuthenticationMap getAuthMap() {
    return authMap;
  }

  public void setAuthMap(AuthenticationMap authMap) {
    this.authMap = authMap;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LoginRequest loginRequest = (LoginRequest) o;
    return Objects.equals(this.providerId, loginRequest.providerId) &&
        Objects.equals(this.authMap, loginRequest.authMap);
  }

  @Override
  public int hashCode() {
    return Objects.hash(providerId, authMap);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LoginRequest {\n");
    
    sb.append("    providerId: ").append(toIndentedString(providerId)).append("\n");
    sb.append("    authMap: ").append(toIndentedString(authMap)).append("\n");
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
