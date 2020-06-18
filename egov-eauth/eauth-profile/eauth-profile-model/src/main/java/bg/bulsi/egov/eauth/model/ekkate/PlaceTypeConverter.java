package bg.bulsi.egov.eauth.model.ekkate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class PlaceTypeConverter implements AttributeConverter<PlaceType, String> {

    @Override
    public String convertToDatabaseColumn(PlaceType attribute) {

        if (attribute == null) {
            return null;
        }

        return attribute.getPlaceName();
    }

    @Override
    public PlaceType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return PlaceType.fromPlaceName(dbData);
    }
}
