package bg.bulsi.egov.saml.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensaml.saml.common.AbstractSAMLObjectBuilder;

import bg.bulsi.egov.saml.LevelOfAssurance;
import bg.bulsi.egov.saml.SAMLeAuthConstants;

public class LevelOfAssuranceBuilder extends AbstractSAMLObjectBuilder<LevelOfAssurance> {

  @Nonnull
  @Override
  public LevelOfAssurance buildObject() {
    return this.buildObject(SAMLeAuthConstants.SAML2_EAUTH_EXT_NS, LevelOfAssurance.TYPE_LOCAL_NAME,
        SAMLeAuthConstants.SAML2_EAUTH_EXT_PREFIX);
  }

  @Nonnull
  @Override
  public LevelOfAssurance buildObject(@Nullable String namespaceURI, @Nonnull String localName,
      @Nullable String namespacePrefix) {
    return new LevelOfAssuranceImpl(namespaceURI, localName, namespacePrefix);
  }
}
