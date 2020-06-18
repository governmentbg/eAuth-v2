
package soap.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AuthenticationMethodsEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AuthenticationMethodsEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="QES"/&gt;
 *     &lt;enumeration value="UN + PWD"/&gt;
 *     &lt;enumeration value="eID"/&gt;
 *     &lt;enumeration value="WS-Security + X.509"/&gt;
 *     &lt;enumeration value="NO_Authentication"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "AuthenticationMethodsEnum")
@XmlEnum
public enum AuthenticationMethodsEnum {

    QES("QES"),
    @XmlEnumValue("UN + PWD")
    UN_PWD("UN + PWD"),
    @XmlEnumValue("eID")
    E_ID("eID"),
    @XmlEnumValue("WS-Security + X.509")
    WS_SECURITY_X_509("WS-Security + X.509"),
    @XmlEnumValue("NO_Authentication")
    NO_AUTHENTICATION("NO_Authentication");
    private final String value;

    AuthenticationMethodsEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AuthenticationMethodsEnum fromValue(String v) {
        for (AuthenticationMethodsEnum c: AuthenticationMethodsEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
