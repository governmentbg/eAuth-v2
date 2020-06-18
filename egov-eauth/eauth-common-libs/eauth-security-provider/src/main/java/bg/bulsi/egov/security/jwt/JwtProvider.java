package bg.bulsi.egov.security.jwt;

import bg.bulsi.egov.security.eauth.userdetails.EauthUserPrincipal;
import bg.bulsi.egov.security.jwt.config.JwtProviderProperties;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@SuppressWarnings("unused")
@Slf4j
@Component
public class JwtProvider {

    private final JwtProviderProperties properties;

    @Autowired
    public JwtProvider(JwtProviderProperties properties) {
        this.properties = properties;
    }

    public String createToken(String nid, String name) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.getTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(nid)
                .setIssuer(name)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, properties.getTokenSecret())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(properties.getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(properties.getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getIssuer();
    }
    
    public Jws<Claims> validateToken(String authToken) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {

        return Jwts.parser().setSigningKey(properties.getTokenSecret()).parseClaimsJws(authToken);
    }

}
