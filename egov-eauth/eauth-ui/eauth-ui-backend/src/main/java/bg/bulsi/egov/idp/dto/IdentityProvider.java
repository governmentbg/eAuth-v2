package bg.bulsi.egov.idp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import io.swagger.annotations.ApiModelProperty;

/**
 * IdentityProvider
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-02-25T16:48:07.018+02:00[Europe/Sofia]")
@JacksonXmlRootElement(localName = "IdentityProvider")
@XmlRootElement(name = "IdentityProvider")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityProvider implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	@JacksonXmlProperty(localName = "id")
	private String id = null;

	@JsonProperty("name")
	@JacksonXmlProperty(localName = "name")
	@Valid
	private Map<String, String> name = null;

	@JsonProperty("tfaRequired")
	@JacksonXmlProperty(localName = "tfaRequired")
	private Boolean tfaRequired = false;

	@JsonProperty("loa")
	@JacksonXmlProperty(localName = "loa")
	private LevelOfAssurance loa = null;

	@JsonProperty("attributes")
	@JacksonXmlProperty(localName = "attributes")
	@Valid
	private List<AuthenticationAttribute> attributes = null;

	public IdentityProvider id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Get id
	 * 
	 * @return id
	 **/
	@ApiModelProperty(value = "")

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public IdentityProvider name(Map<String, String> name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * 
	 * @return name
	 **/
	@ApiModelProperty(value = "")

	public Map<String, String> getName() {
		return name;
	}

	public void setName(Map<String, String> name) {
		this.name = name;
	}

	public IdentityProvider tfaRequired(Boolean tfaRequired) {
		this.tfaRequired = tfaRequired;
		return this;
	}

	/**
	 * Flag for mandatory 2FA authentication.
	 * 
	 * @return tfaRequired
	 **/
	@ApiModelProperty(value = "Flag for mandatory 2FA authentication.")

	public Boolean isTfaRequired() {
		return tfaRequired;
	}

	public void setTfaRequired(Boolean tfaRequired) {
		this.tfaRequired = tfaRequired;
	}

	public IdentityProvider loa(LevelOfAssurance loa) {
		this.loa = loa;
		return this;
	}

	/**
	 * Get loa
	 * 
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

	public IdentityProvider attributes(List<AuthenticationAttribute> attributes) {
		this.attributes = attributes;
		return this;
	}

	public IdentityProvider addAttributesItem(AuthenticationAttribute attributesItem) {
		if (this.attributes == null) {
			this.attributes = new ArrayList<>();
		}
		this.attributes.add(attributesItem);
		return this;
	}

	/**
	 * Get attributes
	 * 
	 * @return attributes
	 **/
	@ApiModelProperty(value = "")
	@Valid
	public List<AuthenticationAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AuthenticationAttribute> attributes) {
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
		IdentityProvider identityProvider = (IdentityProvider) o;
		return Objects.equals(this.id, identityProvider.id) && Objects.equals(this.name, identityProvider.name)
				&& Objects.equals(this.tfaRequired, identityProvider.tfaRequired)
				&& Objects.equals(this.loa, identityProvider.loa)
				&& Objects.equals(this.attributes, identityProvider.attributes);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, tfaRequired, loa, attributes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class IdentityProvider {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    tfaRequired: ").append(toIndentedString(tfaRequired)).append("\n");
		sb.append("    loa: ").append(toIndentedString(loa)).append("\n");
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
