package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Attributte Types and default mask
 */
public enum AttributeType {
	DEFAULT("DEFAULT",""),
	EGN("EGN","PNOBG-"),
	PASS("PASS","PASBG-"),
	IDCARD("IDCARD","IDCBG-"),
	EIK("EIK","TINBG-"),
    EMAIL("EMAIL",""),
    DIGITS("DIGITS",""),
    PHONE("PHONE",""),
    LENGTH20("LENGTH20",""),
	SECRET("SECRET","");

  private String value;
  private String eidPrefix;

  AttributeType(String value, String prefix) {
    this.value = value;
    this.eidPrefix =prefix;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonIgnore
  public String getPrefix() {
	  return this.eidPrefix;
  }
  
  @JsonCreator
  public static AttributeType fromValue(String text) {
    for (AttributeType b : AttributeType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}
