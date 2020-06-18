package bg.bulsi.egov.eauth.saml.keystore;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class RSA {
	
	/*
	 * String from file
	 */

	private static String getKey(String filename) throws IOException {
		// Read key from file
		StringBuilder strKeyPEM = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				strKeyPEM.append(line + "\n");
			}
			
		}
		return strKeyPEM.toString();
	}


	/*
	 * PRIVATE KEY
	 */
	
	public static RSAPrivateKey getPrivateKey(String filename) throws IOException, GeneralSecurityException {
		String privateKeyPEM = getKey(filename);
		return getPrivateKeyFromString(privateKeyPEM);
	}


	public static RSAPrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
		String privateKeyPEM = cleanPrivateKey(key);
		byte[] encoded = Base64.decodeBase64(privateKeyPEM);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPrivateKey privKey = (RSAPrivateKey) kf.generatePrivate(keySpec);
		return privKey;
	}

	/*
	 * PUBLIC KEY
	 */

	public static RSAPublicKey getPublicKey(String filename) throws IOException, GeneralSecurityException {
		String publicKeyPEM = getKey(filename);
		return getPublicKeyFromString(publicKeyPEM);
	}


	public static RSAPublicKey getPublicKeyFromString(String key) throws GeneralSecurityException {
		String publicKeyPEM = cleanPublicKey(key);
		byte[] encoded = Base64.decodeBase64(publicKeyPEM);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
		return pubKey;
	}
	
	/*
	 * CERIFICATES 
	 */

	public static Certificate getCertificate(String filename) throws IOException, GeneralSecurityException {
		String key = getKey(filename);
		return getCertificateFromString(key);
	}


	public static Certificate getCertificateFromString(String key) throws CertificateException {
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		
		String wrappedCert;
		if (key.contains("BEGIN")) {			
			wrappedCert = key;
		} else {
			wrappedCert = wrapCert(key);
		}
	    Certificate cert = certificateFactory.generateCertificate(new ByteArrayInputStream(wrappedCert.getBytes(StandardCharsets.UTF_8)));
	    return cert;
	}

	public static Certificate[] getCertificateArr(Certificate cert) throws CertificateException {
	    ArrayList<Certificate> certs = new ArrayList<>();
	    certs.add(cert);
	    Certificate[] certsObj = certs.toArray(new Certificate[certs.size()]);
	    return certsObj;
	}

	public static Certificate[] getCertificateArr(Certificate ... certs) {
	    return certs;
	}
	
	/*
	 * SIGN
	 */

	public static String sign(PrivateKey privateKey, String message) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initSign(privateKey);
		sign.update(message.getBytes(StandardCharsets.UTF_8));
		return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8);
	}

	/*
	 * VERIFY
	 */

	public static boolean verify(PublicKey publicKey, String message, String signature)
			throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		Signature sign = Signature.getInstance("SHA1withRSA");
		sign.initVerify(publicKey);
		sign.update(message.getBytes(StandardCharsets.UTF_8));
		return sign.verify(Base64.decodeBase64(signature.getBytes(StandardCharsets.UTF_8)));
	}

	/*
	 * ENCRIPT/DECRIPT
	 */
	
	public static String encrypt(String rawText, PublicKey publicKey) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return Base64.encodeBase64String(cipher.doFinal(rawText.getBytes(StandardCharsets.UTF_8)));
	}


	public static String decrypt(String cipherText, PrivateKey privateKey) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return new String(cipher.doFinal(Base64.decodeBase64(cipherText)), StandardCharsets.UTF_8);
	}

	/*
	 * WRAP/CLEAN STRING CERTS
	 */

	private static String wrapPrivateKey(String key) {
		return "-----BEGIN PRIVATE KEY-----\n" + key + "\n-----END PRIVATE KEY-----";
	}
	
	private static String wrapPublicKey(String key) {
		return "-----BEGIN PUBLIC KEY-----\n" + key + "\n-----END PUBLIC KEY-----";
	}

	private static String wrapCert(String key) {
		return "-----BEGIN CERTIFICATE-----\n" + key + "\n-----END CERTIFICATE-----";
	}

	private static String cleanPrivateKey(String key) {
		key = key.replace("-----BEGIN PRIVATE KEY-----", "");
		key = key.replace("-----END PRIVATE KEY-----", "");
		key = key.replace("-----BEGIN RSA PRIVATE KEY-----", "");
		key = key.replace("-----END RSA PRIVATE KEY-----", "");
		return key.replaceAll("\n", "");
		
	}
	
	private static String cleanPublicKey(String key) {
		key = key.replace("-----BEGIN PUBLIC KEY-----", "");
		key = key.replace("-----END PUBLIC KEY-----", "");
		return key.replaceAll("\n", "");
	}

	private static String cleanCert(String key) {
		key = key.replace("-----BEGIN CERTIFICATE-----", "");
		key = key.replace("-----END CERTIFICATE-----", "");
		return key.replaceAll("\n", "");
	}
}
