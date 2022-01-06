package bg.bulsi.egov.saml.impl;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.AbstractSAMLObjectUnmarshaller;
import org.opensaml.saml.saml2.core.Attribute;
import org.w3c.dom.Attr;

import bg.bulsi.egov.saml.AttributeValue;
import bg.bulsi.egov.saml.RequestedAttribute;

public class RequestedAttributeUnmarshaller extends AbstractSAMLObjectUnmarshaller {

	@Override
	protected void processChildElement(final XMLObject parentSAMLObject, final XMLObject childSAMLObject)
			throws UnmarshallingException {

		RequestedAttribute requestedAttribute = (RequestedAttribute) parentSAMLObject;

		if (childSAMLObject instanceof AttributeValue) {
			requestedAttribute.getAttributeValues().add((AttributeValue) childSAMLObject);
		} else {
			super.processChildElement(parentSAMLObject, childSAMLObject);
		}

	}

	@Override
	protected void processAttribute(final XMLObject samlObject, final Attr attribute) throws UnmarshallingException {

		final RequestedAttribute attrib = (RequestedAttribute) samlObject;
		
		if (attribute.getNamespaceURI() == null) {
			if (attribute.getLocalName().equals(Attribute.NAME_ATTTRIB_NAME)) {
				attrib.setName(attribute.getValue());
			} else if (attribute.getLocalName().equals(Attribute.NAME_FORMAT_ATTRIB_NAME)) {
				attrib.setNameFormat(attribute.getValue());
			} else if (attribute.getLocalName().equals(Attribute.FRIENDLY_NAME_ATTRIB_NAME)) {
				attrib.setFriendlyName(attribute.getValue());
			} else if (attribute.getLocalName().equals("isRequired")) {
				attrib.setRequired(Boolean.valueOf(attribute.getValue()));
			} else {
				super.processAttribute(samlObject, attribute);
			}
		} else {
			// processUnknownAttribute(attrib, attribute);
		}
	}

}
