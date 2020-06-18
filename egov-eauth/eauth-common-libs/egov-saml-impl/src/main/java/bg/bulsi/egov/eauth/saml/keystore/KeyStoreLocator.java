package bg.bulsi.egov.eauth.saml.keystore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import org.apache.commons.io.IOUtils;

public class KeyStoreLocator {

  private static CertificateFactory certificateFactory;

  static {
    try {
      certificateFactory = CertificateFactory.getInstance("X.509");
    } catch (CertificateException e) {
      throw new RuntimeException(e);
    }
  }

  public static KeyStore createKeyStore(String pemPassPhrase) {
    try {

      KeyStore keyStore = KeyStore.getInstance("JKS");
      keyStore.load(null, pemPassPhrase.toCharArray());
      return keyStore;

    } catch (Exception e) {
      //too many exceptions we can't handle, so brute force catch
      throw new RuntimeException(e);
    }
  }

  //privateKey must be in the DER unencrypted PKCS#8 format. See README.md
  public static void addPrivateKey(KeyStore keyStore, String alias, String privateKey, String certificate, String password)
      throws IOException, GeneralSecurityException {

    char[] passwordChars = password.toCharArray();

    RSAPrivateKey privKey = RSA.getPrivateKeyFromString(privateKey);
    // RSAPublicKey pubKey = RSA.getPublicKeyFromString(privateKey);

    Certificate cert = RSA.getCertificateFromString(certificate);
    
    keyStore.setKeyEntry(alias, privKey, passwordChars, RSA.getCertificateArr(cert));
  }

}
