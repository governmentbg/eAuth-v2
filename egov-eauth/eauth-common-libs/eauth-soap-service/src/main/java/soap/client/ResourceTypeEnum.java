
package soap.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResourceTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ResourceTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="ADMINISTRATION"/&gt;
 *     &lt;enumeration value="INFORMATION_SYSTEM"/&gt;
 *     &lt;enumeration value="SERVICE"/&gt;
 *     &lt;enumeration value="SERVICE_VERSION"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "ResourceTypeEnum", namespace = "http://egov.bg/regres/ws/v1")
@XmlEnum
public enum ResourceTypeEnum {

    ADMINISTRATION,
    INFORMATION_SYSTEM,
    SERVICE,
    SERVICE_VERSION;

    public String value() {
        return name();
    }

    public static ResourceTypeEnum fromValue(String v) {
        return valueOf(v);
    }

}
