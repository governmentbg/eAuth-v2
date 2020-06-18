package bg.bulsi.egov.eauth.metadata.samlobjects;


import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.AbstractSAMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class SPTypeImpl.
 */
public class SPTypeImpl extends AbstractSAMLObject implements SPType {

    /** The type. */
    private String spType;

    /**
     * Instantiates a new Service provider sector implementation.
     *
     * @param namespaceURI the namespace URI
     * @param elementLocalName the element local name
     * @param namespacePrefix the namespace prefix
     */
    protected SPTypeImpl(final String namespaceURI,
                         final String elementLocalName, final String namespacePrefix) {
	super(namespaceURI, elementLocalName, namespacePrefix);
    }


    /**
     * Gets the service provider sector.
     *
     * @return the SP sector
     *
     * @see SPType#getSPType()
     */
    public final String getSPType() {
	return spType;
    }


    /**
     * Sets the  sector type.
     *
     * @param newSpType the new sector type
     */
    public final void setSPType(final String newSpType) {
	this.spType = prepareForAssignment(this.spType, newSpType);
    }


    /**
     * Gets the ordered children.
     *
     * @return the ordered children
     */
    public final List<XMLObject> getOrderedChildren() {
        return new ArrayList<>();
    }

    @Override
    public int hashCode() {// NOSONAR
        throw new UnsupportedOperationException("hashCode method not implemented");
    }

}
