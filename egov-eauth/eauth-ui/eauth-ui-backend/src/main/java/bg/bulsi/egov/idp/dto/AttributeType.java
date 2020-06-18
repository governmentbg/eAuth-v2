package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Attributte Types and default mask
 */
public enum AttributeType {
  SECRET("SECRET"),
    EMAIL("EMAIL"),
    DIGITS("DIGITS"),
    EGN("EGN"),
    PHONE("PHONE"),
    LENGTH20("LENGTH20"),
    DEFAULT("DEFAULT");

  private String value;

  AttributeType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
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
