package bg.bulsi.egov.idp.dto;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.swagger.annotations.ApiModelProperty;

/**
 * OTPresponse
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:48:07.018+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "OTPresponse")
@XmlRootElement(name = "OTPresponse")
@XmlAccessorType(XmlAccessType.FIELD)public class OTPresponse  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("tid")
  @JacksonXmlProperty(localName = "tid")
  private String tid = null;

  @JsonProperty("timestamp")
  @JacksonXmlProperty(localName = "timestamp")
  private Long timestamp = null;

  @JsonProperty("method")
  @JacksonXmlProperty(localName = "method")
  private OTPMethod method = null;

  @JsonProperty("message")
  @JacksonXmlProperty(localName = "message")
  private String message = null;

  @JsonProperty("qrCode")
  @JacksonXmlProperty(localName = "qrCode")
  private SecretMetadata qrCode = null;

  public OTPresponse tid(String tid) {
    this.tid = tid;
    return this;
  }

  /**
   * Transaction Id for this code generation
   * @return tid
  **/
  @ApiModelProperty(required = true, value = "Transaction Id for this code generation")
      @NotNull

  @Size(min=8,max=32)   public String getTid() {
    return tid;
  }

  public void setTid(String tid) {
    this.tid = tid;
  }

  public OTPresponse timestamp(Long timestamp) {
    this.timestamp = timestamp;
    return this;
  }

  /**
   * Get timestamp
   * @return timestamp
  **/
  @ApiModelProperty(value = "")
  
    public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public OTPresponse method(OTPMethod method) {
    this.method = method;
    return this;
  }

  /**
   * Get method
   * @return method
  **/
  @ApiModelProperty(required = true, value = "")
      @NotNull

    @Valid
    public OTPMethod getMethod() {
    return method;
  }

  public void setMethod(OTPMethod method) {
    this.method = method;
  }

  public OTPresponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
  **/
  @ApiModelProperty(value = "")
  
    public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public OTPresponse qrCode(SecretMetadata qrCode) {
    this.qrCode = qrCode;
    return this;
  }

  /**
   * Get qrCode
   * @return qrCode
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public SecretMetadata getQrCode() {
    return qrCode;
  }

  public void setQrCode(SecretMetadata qrCode) {
    this.qrCode = qrCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OTPresponse otPresponse = (OTPresponse) o;
    return Objects.equals(this.tid, otPresponse.tid) &&
        Objects.equals(this.timestamp, otPresponse.timestamp) &&
        Objects.equals(this.method, otPresponse.method) &&
        Objects.equals(this.message, otPresponse.message) &&
        Objects.equals(this.qrCode, otPresponse.qrCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tid, timestamp, method, message, qrCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OTPresponse {\n");
    
    sb.append("    tid: ").append(toIndentedString(tid)).append("\n");
    sb.append("    timestamp: ").append(toIndentedString(timestamp)).append("\n");
    sb.append("    method: ").append(toIndentedString(method)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    qrCode: ").append(toIndentedString(qrCode)).append("\n");
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
