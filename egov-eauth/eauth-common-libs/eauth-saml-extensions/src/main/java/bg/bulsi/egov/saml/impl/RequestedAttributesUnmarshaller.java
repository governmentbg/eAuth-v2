package bg.bulsi.egov.saml.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;

import bg.bulsi.egov.saml.RequestedAttribute;
import bg.bulsi.egov.saml.RequestedAttributes;

public class RequestedAttributesUnmarshaller extends AbstractSAMLObjectUnmarshaller {

	@Override
	protected void processChildElement(final XMLObject parentSAMLObject, final XMLObject childSAMLObject)
			throws UnmarshallingException {
		
		RequestedAttributes requestedAttributes = (RequestedAttributes) parentSAMLObject;
		
		if (childSAMLObject instanceof RequestedAttribute) {
			requestedAttributes.getRequestedAttributes().add((RequestedAttribute) childSAMLObject);
		} else {
			super.processChildElement(parentSAMLObject, childSAMLObject);
		}

	}
}
