//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.06.08 at 06:01:55 PM EEST 
//


package bg.bulsi.egov.saml.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserNameFormatEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UserNameFormatEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="urn:egov:bg:eauth:1.0:username:simple"/>
 *     &lt;enumeration value="urn:egov:bg:eauth:1.0:username:canonical"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UserNameFormatEnum")
@XmlEnum
public enum UserNameFormatEnum {

    @XmlEnumValue("urn:egov:bg:eauth:1.0:username:simple")
    URN_EGOV_BG_EAUTH_1_0_USERNAME_SIMPLE("urn:egov:bg:eauth:1.0:username:simple"),
    @XmlEnumValue("urn:egov:bg:eauth:1.0:username:canonical")
    URN_EGOV_BG_EAUTH_1_0_USERNAME_CANONICAL("urn:egov:bg:eauth:1.0:username:canonical");
    private final String value;

    UserNameFormatEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UserNameFormatEnum fromValue(String v) {
        for (UserNameFormatEnum c: UserNameFormatEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
