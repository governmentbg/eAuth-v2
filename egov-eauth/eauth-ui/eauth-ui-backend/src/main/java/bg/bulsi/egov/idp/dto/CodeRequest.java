package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.springframework.validation.annotation.Validated;

/**
 * CodeRequest
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:48:07.018+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "CodeRequest")
@XmlRootElement(name = "CodeRequest")
@XmlAccessorType(XmlAccessType.FIELD)public class CodeRequest  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("ctId")
  @JacksonXmlProperty(localName = "ctId")
  private String ctId = null;

  @JsonProperty("newCodeType")
  @JacksonXmlProperty(localName = "newCodeType")
  private OTPMethod newCodeType = null;

  public CodeRequest ctId(String ctId) {
    this.ctId = ctId;
    return this;
  }

  /**
   * Current transaction ID to be replaced
   * @return ctId
  **/
  @ApiModelProperty(value = "Current transaction ID to be replaced")
  
  @Size(min=8,max=32)   public String getCtId() {
    return ctId;
  }

  public void setCtId(String ctId) {
    this.ctId = ctId;
  }

  public CodeRequest newCodeType(OTPMethod newCodeType) {
    this.newCodeType = newCodeType;
    return this;
  }

  /**
   * Get newCodeType
   * @return newCodeType
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public OTPMethod getNewCodeType() {
    return newCodeType;
  }

  public void setNewCodeType(OTPMethod newCodeType) {
    this.newCodeType = newCodeType;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CodeRequest codeRequest = (CodeRequest) o;
    return Objects.equals(this.ctId, codeRequest.ctId) &&
        Objects.equals(this.newCodeType, codeRequest.newCodeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ctId, newCodeType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CodeRequest {\n");
    
    sb.append("    ctId: ").append(toIndentedString(ctId)).append("\n");
    sb.append("    newCodeType: ").append(toIndentedString(newCodeType)).append("\n");
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
