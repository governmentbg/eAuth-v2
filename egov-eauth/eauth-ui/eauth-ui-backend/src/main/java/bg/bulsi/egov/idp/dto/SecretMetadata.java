package bg.bulsi.egov.idp.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import javax.xml.bind.annotation.*;

/**
 * SecretMetadata
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-05-26T13:27:57.613+03:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "Secret")
@XmlRootElement(name = "Secret")
@XmlAccessorType(XmlAccessType.FIELD)public class SecretMetadata  implements Serializable  {
  private static final long serialVersionUID = 1L;

  @JsonProperty("secretKey")
  @JacksonXmlProperty(localName = "secretKey")
  private String secretKey = null;

  @JsonProperty("qrImage")
  @JacksonXmlProperty(localName = "qrImage")
  private byte[] qrImage = null;

  public SecretMetadata secretKey(String secretKey) {
    this.secretKey = secretKey;
    return this;
  }

  /**
   * Get secretKey
   * @return secretKey
  **/
  @ApiModelProperty(value = "")
  
    public String getSecretKey() {
    return secretKey;
  }

  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  public SecretMetadata qrImage(byte[] qrImage) {
    this.qrImage = qrImage;
    return this;
  }

  /**
   * Base64-encoded contents of the QR code image
   * @return qrImage
  **/
  @ApiModelProperty(value = "Base64-encoded contents of the QR code image")
  
    public byte[] getQrImage() {
    return qrImage;
  }

  public void setQrImage(byte[] qrImage) {
    this.qrImage = qrImage;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SecretMetadata secretMetadata = (SecretMetadata) o;
    return Objects.equals(this.secretKey, secretMetadata.secretKey) &&
        Objects.equals(this.qrImage, secretMetadata.qrImage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(secretKey, qrImage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SecretMetadata {\n");
    
    sb.append("    secretKey: ").append(toIndentedString(secretKey)).append("\n");
    sb.append("    qrImage: ").append(toIndentedString(qrImage)).append("\n");
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
