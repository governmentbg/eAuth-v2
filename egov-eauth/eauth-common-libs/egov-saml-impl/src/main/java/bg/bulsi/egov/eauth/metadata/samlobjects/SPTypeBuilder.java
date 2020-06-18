package bg.bulsi.egov.eauth.metadata.samlobjects;


import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

/**
 * The Class SPTypeBuilder.
 */
public class SPTypeBuilder extends AbstractSAMLObjectBuilder<SPType> {

    /**
     * Builds the object SPType.
     *
     * @return the sector type.
     */
    public final SPType buildObject() {
	return buildObject(SPType.DEF_ELEMENT_NAME);
    }

    /**
     * Builds the object SPType.
     *
     * @param namespaceURI the namespace uri
     * @param localName the local name
     * @param namespacePrefix the namespace prefix
     * @return the sector type
     */
    public final SPType buildObject(final String namespaceURI,
	    final String localName, final String namespacePrefix) {
	return new SPTypeImpl(namespaceURI, localName, namespacePrefix);
    }
}