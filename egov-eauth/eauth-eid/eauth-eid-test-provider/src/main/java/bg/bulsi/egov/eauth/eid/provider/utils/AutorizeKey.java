package bg.bulsi.egov.eauth.eid.provider.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutorizeKey {
	

	public static String encodeHmacSHA256(String message, String secret) {
		
		MacAlgorithms algorithm = MacAlgorithms.HmacSHA256;
		return encode(algorithm, message, secret);
		
	}

	public static String encode(MacAlgorithms algorithm, String message, String secret) {
		
		String hash = null;
		
		try {

			Mac sha256_HMAC = Mac.getInstance(algorithm.name());
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), algorithm.name());
			sha256_HMAC.init(secret_key);

			String hexMessage = HexMsg.toHex(message);
			
			byte[] encodedhash = sha256_HMAC.doFinal(hexMessage.getBytes());
			hash = Base64.encodeBase64String(encodedhash);
			
			log.debug(hash);
			

		} catch (Exception e) {

			log.error(e.getLocalizedMessage());

		}
		
		return hash;

	}
	
	private static class HexMsg {
		
		public static String toHex(String msg) {
			
		    return Hex.encodeHexString(msg.getBytes(Charsets.UTF_8));
		    
		}
		
	}
	
	public enum MacAlgorithms {
		HmacMD5, 	
		HmacSHA1,
		HmacSHA256,
		HmacSHA384,
		HmacSHA512,
		PBEWithHmacMD5, 	
		PBEWithHmacSHA1,
		PBEWithHmacSHA256,
		PBEWithHmacSHA384,
		PBEWithHmacSHA512,
		PBEWith
	}

	public static void main(String[] args) {
		
		AutorizeKey autorizeKey = new AutorizeKey();
		String genKey = autorizeKey.encodeHmacSHA256("fullAuthenticationRequest", "vendorKey");
	}
	
}
