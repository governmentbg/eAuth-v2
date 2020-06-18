package bg.bulsi.egov.eauth.metadata.samlobjects;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.SAMLObject;

/**
 * The Interface SPType. - @see "eidas:SPType md:Extensions" element of SAML metadata. The @see "eidas:SPType" element can contain the values "public" or "private" only
 */
public interface SPType extends SAMLObject {

    /** The Constant DEFAULT_ELEMENT_LOCAL_NAME. */
    String DEF_LOCAL_NAME = "SPType";

    /** The Constant DEFAULT_ELEMENT_NAME. */
    QName DEF_ELEMENT_NAME = new QName(SAMLMetadataCore.EAUTH_SAML_NS.getValue(), DEF_LOCAL_NAME,
            SAMLMetadataCore.EIDAS10_SAML_PREFIX.getValue());

    /** The Constant TYPE_LOCAL_NAME. */
    String TYPE_LOCAL_NAME = "SPType";

    /** The default value of SPType. */
    String DEFAULT_VALUE = "public";

    /** The Constant TYPE_NAME. */
    QName TYPE_NAME = new QName(SAMLMetadataCore.EAUTH_SAML_NS.getValue(), TYPE_LOCAL_NAME,
            SAMLMetadataCore.EAUTH_SAML_NS.getValue());

    /**
     * Gets the sector type.
     *
     * @return the sector provider
     */
    String getSPType();

    /**
     * Sets the request's sector type.
     *
     * @param spType the new service type
     */
    void setSPType(String spType);
}
