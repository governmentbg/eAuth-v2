package bg.bulsi.egov.saml.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.RequestedAttributes;
import bg.bulsi.egov.saml.SAMLeAuthConstants;

public class RequestedAttributesBuilder extends AbstractSAMLObjectBuilder<RequestedAttributes> {

  @Nonnull
  @Override
  public RequestedAttributes buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, RequestedAttributes.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public RequestedAttributes buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new RequestedAttributesImpl(namespaceURI, localName, namespacePrefix);
  }
}
