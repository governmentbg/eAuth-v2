package bg.bulsi.egov.saml.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.AttributeValue;
import bg.bulsi.egov.saml.SAMLeAuthConstants;

public class AttributeValueBuilder extends AbstractSAMLObjectBuilder<AttributeValue> {

  @Nonnull
  @Override
  public AttributeValue buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, AttributeValue.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public AttributeValue buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new AttributeValueImpl(namespaceURI, localName, namespacePrefix);
  }
}
