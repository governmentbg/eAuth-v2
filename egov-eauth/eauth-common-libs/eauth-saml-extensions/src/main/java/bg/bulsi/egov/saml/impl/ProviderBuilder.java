package bg.bulsi.egov.saml.impl;

import bg.bulsi.egov.saml.Provider;
import bg.bulsi.egov.saml.SAMLeAuthConstants;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

public class ProviderBuilder extends AbstractSAMLObjectBuilder<Provider> {

  @Nonnull
  @Override
  public Provider buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, Provider.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public Provider buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new ProviderImpl(namespaceURI, localName, namespacePrefix);
  }
}
