package bg.bulsi.egov.saml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;

import bg.bulsi.egov.saml.RequestedAttribute;
import bg.bulsi.egov.saml.RequestedAttributes;

public class RequestedAttributesImpl extends AbstractSAMLObject implements RequestedAttributes {

	private  List<RequestedAttribute> requestedAttributes;
	

	/**
	 * Constructor.
	 *
	 * @param namespaceURI     the namespace the element is in
	 * @param elementLocalName the local name of the XML element this Object
	 *                         represents
	 * @param namespacePrefix  the prefix for the given namespace
	 */
	protected RequestedAttributesImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
		super(namespaceURI, elementLocalName, namespacePrefix);
	}

	@Override
	public List<RequestedAttribute> getRequestedAttributes() {
		if (this.requestedAttributes == null) {
			this.requestedAttributes = new ArrayList<>();
		}
		return requestedAttributes;
	}

	@Override
	public void setRequestedAttributes(List<RequestedAttribute> requestedAttributes) {
		this.requestedAttributes = prepareForAssignment(this.requestedAttributes, requestedAttributes);
	}

	@Nullable
	@Override
	public List<XMLObject> getOrderedChildren() {
		final ArrayList<XMLObject> children = new ArrayList<>();

		if (this.requestedAttributes != null) {
			requestedAttributes.stream().forEach(c -> children.add(c));
		}

		if (children.isEmpty()) {
			return null;
		}

		return Collections.unmodifiableList(children);
	}

}
