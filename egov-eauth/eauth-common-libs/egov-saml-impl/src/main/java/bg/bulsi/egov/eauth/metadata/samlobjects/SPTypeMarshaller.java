package bg.bulsi.egov.eauth.metadata.samlobjects;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.common.xml.XMLHelper;

/**
 * The Class SPTypeMarshaller.
 *
 */
public class SPTypeMarshaller extends AbstractSAMLObjectMarshaller {

    /**
     * Marshall element content.
     *
     * @param samlObject the SAML object
     * @param domElement the DOM element
     * @throws MarshallingException the marshalling exception
     */
    protected final void marshallElementContent(final XMLObject samlObject,
	    final Element domElement) throws MarshallingException {
	final SPType spType = (SPType) samlObject;
	XMLHelper.appendTextContent(domElement, spType.getSPType());
    }
}