package bg.bulsi.egov.eauth.metadata.samlobjects;

public enum SAMLMetadataCore {
    LEVEL_OF_ASSURANCE_NAME("urn:oasis:names:tc:SAML:attribute:assurance-certification"),
    PROTOCOL_VERSION_URI("urn:bg:egov:eauth:2.0:saml:ext"),
    APPLICATION_IDENTIFIER("http://eauth.egov.bg/attributes/citizen"),
    SAML_NAME_ID_FORMAT_PERSISTENT("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"),

    SAML_NAME_ID_FORMAT_TRANSIENT("urn:oasis:names:tc:SAML:2.0:nameid-format:transient"),

    SAML_NAME_ID_FORMAT_UNSPECIFIED("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"),

    /** The consent authentication request. */
    CONSENT_AUTHN_REQ("consentAuthnRequest"),

    /** The consent authentication response. */
    CONSENT_AUTHN_RES("consentAuthnResponse"),

    /** The FORC e_ auth n_ tag. */
    FORCE_AUTHN_TAG("forceAuthN"),

    /** The I s_ passiv e_ tag. */
    IS_PASSIVE_TAG("isPassive"),

    /** The FORMA t_ entity. */
    FORMAT_ENTITY("formatEntity"),

    /** The PRO t_ bindin g_ tag. */
    PROT_BINDING_TAG("protocolBinding"),

    /** The ASSER t_ con s_ tag. */
    ASSERT_CONS_TAG("assertionConsumerServiceURL"),

    /** The REQUESTE r_ tag. */
    REQUESTER_TAG("requester"),

    /** The RESPONDE r_ tag. */
    RESPONDER_TAG("responder"),

    /** The validateSignature tag. */
    VALIDATE_SIGNATURE_TAG("validateSignature"),

    /** The EIDAS10 saml extension ns. */
    EAUTH_SAML_NS("urn:bg:egov:eauth:2.0:saml:ext"),

    /** The EIDAS10_ prefix. */
    EAUTH_SAML_PREFIX("egovbga"),

     /** The ON e_ tim e_ use. */
    ONE_TIME_USE("oneTimeUse"),
    ;

    /** The value. */
    private String value;

    /**
     * Instantiates a new sAML core.
     *
     * @param fullName the full name
     */
    SAMLMetadataCore(final String fullName) {
	this.value = fullName;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
	return value;
    }
}
