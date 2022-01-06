package bg.bulsi.egov.saml.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.RequestedAttribute;
import bg.bulsi.egov.saml.SAMLeAuthConstants;

public class RequestedAttributeBuilder extends AbstractSAMLObjectBuilder<RequestedAttribute> {

  @Nonnull
  @Override
  public RequestedAttribute buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, RequestedAttribute.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public RequestedAttribute buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new RequestedAttributeImpl(namespaceURI, localName, namespacePrefix);
  }
}
