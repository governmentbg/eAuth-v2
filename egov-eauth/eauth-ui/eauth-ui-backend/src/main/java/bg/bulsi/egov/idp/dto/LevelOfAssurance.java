package bg.bulsi.egov.idp.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Available standart EU assurance level
 */
public enum LevelOfAssurance {
	LOW("LOW"), 
	SUBSTANTIAL("SUBSTANTIAL"), 
	HIGH("HIGH");

	private String value;

	LevelOfAssurance(String value) {
		this.value = value;
	}

	@Override
	@JsonValue
	public String toString() {
		return String.valueOf(value);
	}

	@JsonCreator
	public static LevelOfAssurance fromValue(String text) {
		for (LevelOfAssurance b : LevelOfAssurance.values()) {
			if (String.valueOf(b.value).equals(text)) {
				return b;
			}
		}
		return null;
	}
}
