package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * CommonAuthException
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:48:07.018+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "authException")
@XmlRootElement(name = "authException")
@XmlAccessorType(XmlAccessType.FIELD)public class CommonAuthException  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("code")
  @JacksonXmlProperty(localName = "code")
  private String code = null;

  @JsonProperty("errorMessage")
  @JacksonXmlProperty(localName = "errorMessage")
  private String errorMessage = null;

  public CommonAuthException code(String code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
  **/
  @ApiModelProperty(required = true, value = "")
      @NotNull

    public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public CommonAuthException errorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  /**
   * Get errorMessage
   * @return errorMessage
  **/
  @ApiModelProperty(required = true, value = "")
      @NotNull

    public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CommonAuthException commonAuthException = (CommonAuthException) o;
    return Objects.equals(this.code, commonAuthException.code) &&
        Objects.equals(this.errorMessage, commonAuthException.errorMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, errorMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CommonAuthException {\n");
    
    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    errorMessage: ").append(toIndentedString(errorMessage)).append("\n");
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
