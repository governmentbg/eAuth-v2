package bg.bulsi.egov.idp.utils;

import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

public class JwtUtil {
	
	
	private JwtUtil() {
		// Util class
	}

	
	public static Claims decodeJWT(String jwt, String base64secretKey) throws SignatureException, MalformedJwtException {
	    //This line will throw an exception if it is not a signed JWS (as expected)
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(base64secretKey);
	    
	    Claims claims = Jwts.parser()
	            .setSigningKey(apiKeySecretBytes)
	            .parseClaimsJws(jwt).getBody();
	    return claims;
	}
	
	
	public static boolean verifyJWT(String jwt) {
		return Jwts.parser().isSigned(jwt);
	}
	
}
