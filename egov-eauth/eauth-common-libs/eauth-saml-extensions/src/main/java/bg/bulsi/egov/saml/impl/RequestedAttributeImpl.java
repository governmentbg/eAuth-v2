package bg.bulsi.egov.saml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;

import bg.bulsi.egov.saml.AttributeValue;
import bg.bulsi.egov.saml.RequestedAttribute;

public class RequestedAttributeImpl extends AbstractSAMLObject implements RequestedAttribute {

	private List<AttributeValue> attributeValues;
	
	private String name;

	private String nameFormat;

	private String friendlyName;

	private Boolean isRequired;

	/**
	 * Constructor.
	 *
	 * @param namespaceURI     the namespace the element is in
	 * @param elementLocalName the local name of the XML element this Object
	 *                         represents
	 * @param namespacePrefix  the prefix for the given namespace
	 */
	protected RequestedAttributeImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
		super(namespaceURI, elementLocalName, namespacePrefix);
	}

	@Override
	public List<AttributeValue> getAttributeValues() {
		if (this.attributeValues == null) {
			this.attributeValues = new ArrayList<>();
		}
		return attributeValues;
	}

	@Override
	public void setAttributeValues(List<AttributeValue> attributeValues) {
		this.attributeValues = prepareForAssignment(this.attributeValues, attributeValues);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = prepareForAssignment(this.name, name);
	}

	@Override
	public String getNameFormat() {
		return nameFormat;
	}

	@Override
	public void setNameFormat(String nameFormat) {
		this.nameFormat = prepareForAssignment(this.nameFormat, nameFormat);
	}

	@Override
	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = prepareForAssignment(this.friendlyName, friendlyName);
	}

	@Override
	public Boolean isRequired() {
		if (isRequired == null) {
			return Boolean.FALSE;
		}
		return isRequired;
	}

	@Override
	public void setRequired(Boolean isRequired) {
		this.isRequired = isRequired;
	}

	@Nullable
	@Override
	public List<XMLObject> getOrderedChildren() {
		final ArrayList<XMLObject> children = new ArrayList<>();

		if (this.attributeValues != null) {
			attributeValues.stream().forEach(c -> children.add(c));
		}
		
		if (children.isEmpty()) {
			return null;
		}

		return Collections.unmodifiableList(children);
	}

}
