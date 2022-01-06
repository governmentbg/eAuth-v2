package bg.bulsi.egov.saml.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectMarshaller;
import org.opensaml.saml.saml2.core.Attribute;
import org.w3c.dom.Element;

import bg.bulsi.egov.saml.RequestedAttribute;

public class RequestedAttributeMarshaller extends AbstractSAMLObjectMarshaller {

	@Override
	protected void marshallAttributes(final XMLObject samlElement, final Element domElement)
            throws MarshallingException {
        final RequestedAttribute attribute = (RequestedAttributeImpl) samlElement;

        if (attribute.getName() != null) {
            domElement.setAttributeNS(null, Attribute.NAME_ATTTRIB_NAME, attribute.getName());
        }

        if (attribute.getNameFormat() != null) {
            domElement.setAttributeNS(null, Attribute.NAME_FORMAT_ATTRIB_NAME, attribute.getNameFormat());
        }

        if (attribute.getFriendlyName() != null) {
            domElement.setAttributeNS(null, Attribute.FRIENDLY_NAME_ATTRIB_NAME, attribute.getFriendlyName());
        }

        if (attribute.isRequired() != null) {
        	domElement.setAttributeNS(null, "isRequired", attribute.isRequired().toString());
        }
        
        // marshallUnknownAttributes(attribute, domElement);
    }
}
