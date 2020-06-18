package bg.bulsi.egov.eauth.saml.keystore;

import java.security.cert.X509Certificate;
import java.util.Set;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.CredentialResolver;

public interface KeyManager extends CredentialResolver {

  /**
   * Returns Credential object used to sign the messages issued by this entity. Public, X509 and
   * Private keys are set in the credential.
   *
   * @param keyName name of the key to use, in case of null default key is used
   * @return credential
   */
  Credential getCredential(String keyName);

  /**
   * Returns Credential object used to sign the messages issued by this entity. Public, X509 and
   * Private keys are set in the credential.
   *
   * @return credential
   */
  Credential getDefaultCredential();

  /**
   * Method provides name of the credential which should be used by default when no other is
   * specified. It must be possible to call getCredential with the returned name in order to obtain
   * Credential value.
   *
   * @return default credential name
   */
  String getDefaultCredentialName();

  /**
   * Method provides list of all credentials available in the storage.
   *
   * @return available credentials
   */
  Set<String> getAvailableCredentials();

  /**
   * Returns certificate with the given alias from the keystore.
   *
   * @param alias alias of certificate to find
   * @return certificate with the given alias or null if not found
   */
  X509Certificate getCertificate(String alias);

}
