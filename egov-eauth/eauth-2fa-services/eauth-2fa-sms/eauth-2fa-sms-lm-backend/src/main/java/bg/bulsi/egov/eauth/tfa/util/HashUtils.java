package bg.bulsi.egov.eauth.tfa.util;

import static org.apache.commons.codec.digest.HmacAlgorithms.HMAC_SHA_512;
import org.apache.commons.codec.digest.HmacUtils;

public class HashUtils {

	private HashUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String sha512Hash(String secret, String data) {
		return new HmacUtils(HMAC_SHA_512, secret).hmacHex(data);
	}
}
