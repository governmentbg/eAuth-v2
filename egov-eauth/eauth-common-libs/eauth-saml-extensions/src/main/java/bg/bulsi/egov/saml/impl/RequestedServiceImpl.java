package bg.bulsi.egov.saml.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;

import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.RequestedService;
import bg.bulsi.egov.saml.Service;

public class RequestedServiceImpl extends AbstractSAMLObject implements RequestedService {

	private Service service;
	private Provider provider;
	private LevelOfAssurance levelOfAssurance;

	/**
	 * Constructor.
	 *
	 * @param namespaceURI     the namespace the element is in
	 * @param elementLocalName the local name of the XML element this Object
	 *                         represents
	 * @param namespacePrefix  the prefix for the given namespace
	 */
	protected RequestedServiceImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
		super(namespaceURI, elementLocalName, namespacePrefix);
	}

	@Override
	public Service getService() {
		return this.service;
	}

	@Override
	public void setService(Service service) {
		this.service = prepareForAssignment(this.service, service);
	}

	@Override
	public Provider getProvider() {
		return this.provider;
	}

	@Override
	public void setProvider(Provider provider) {
		this.provider = prepareForAssignment(this.provider, provider);
	}

	@Override
	public LevelOfAssurance getLevelOfAssurance() {
		return this.levelOfAssurance;
	}

	@Override
	public void setLevelOfAssurance(LevelOfAssurance levelOfAssurance) {
		this.levelOfAssurance = prepareForAssignment(this.levelOfAssurance, levelOfAssurance);
	}

	@Nullable
	@Override
	public List<XMLObject> getOrderedChildren() {
		final ArrayList<XMLObject> children = new ArrayList<>();

		if (this.service != null) {
			children.add(service);
		}

		if (this.provider != null) {
			children.add(provider);
		}

		if (this.levelOfAssurance != null) {
			children.add(levelOfAssurance);
		}

		if (children.isEmpty()) {
			return null;
		}

		return Collections.unmodifiableList(children);
	}

}
