package bg.bulsi.egov.eauth.model.ekkate;

import lombok.Getter;
import lombok.Setter;

public enum PlaceType {

    VILLAGE("с."),
    CITY("гр."),
    MONASTERY("манастир"),
    DISTRICT("област"),
    MUNICIPALITY("община"),
    HOLIDAY_VILLAGE("в.с."),
    RESORT("к.к.");

    @Getter
    @Setter
    private String placeName;

    PlaceType(String name) {
        this.placeName = name;
    }

    public static PlaceType fromPlaceName(String code) {
        for (PlaceType status : PlaceType.values()) {
            if (status.getPlaceName().equals(code)) {
                return status;
            }
        }
        throw new UnsupportedOperationException(
                "The place name: " + code + " is not supported!");
    }

}
