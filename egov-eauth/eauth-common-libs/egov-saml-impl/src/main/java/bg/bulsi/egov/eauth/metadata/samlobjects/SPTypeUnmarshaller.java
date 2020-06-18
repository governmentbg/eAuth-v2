package bg.bulsi.egov.eauth.metadata.samlobjects;


import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;

/**
 * The Class SPTypeUnmarshaller.
 */
public class SPTypeUnmarshaller extends AbstractSAMLObjectUnmarshaller {


    /**
     * Process element content.
     *
     * @param samlObject the SAML object
     * @param elementContent the element content
     */
    protected final void processElementContent(final XMLObject samlObject,
	    final String elementContent) {
	final SPType spSector = (SPType) samlObject;
	spSector.setSPType(elementContent);
    }
}