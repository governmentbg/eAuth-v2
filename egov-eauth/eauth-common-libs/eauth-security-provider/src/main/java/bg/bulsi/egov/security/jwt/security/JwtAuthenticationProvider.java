package bg.bulsi.egov.security.jwt.security;

import bg.bulsi.egov.security.jwt.JwtProvider;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    protected JwtProvider jwtProvider;

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) {

        Assert.notNull(authentication, "Authentication is missing");

        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication,
                "This method only accepts JwtAuthenticationToken");

        String jwtToken = authentication.getName();

        if (authentication.getPrincipal() == null || jwtToken == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication token is missing");
        }


        try {
            Jws<Claims> jwt = jwtProvider.validateToken(jwtToken);
            return new JwtAuthenticationToken(jwt, null, null);

        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token expired");
        } catch (UnsupportedJwtException e) {
            throw new InternalAuthenticationServiceException("Unsupported JWT token");
        } catch (MalformedJwtException e) {
            throw new InternalAuthenticationServiceException("Invalid JWT token");
        } catch (SignatureException e) {
            throw new InternalAuthenticationServiceException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            throw new InternalAuthenticationServiceException("JWT claims string is empty.");
        }


/*
        final SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(jwtToken);

            boolean isVerified = signedJWT.verify(new MACVerifier(SecurityConstant.JWT_SECRET.getBytes()));

            if(!isVerified){
                throw new BadCredentialsException("Invalid token signature");
            }

            //is token expired ?
            LocalDateTime expirationTime = LocalDateTime.ofInstant(
                    signedJWT.getJWTClaimsSet().getExpirationTime().toInstant(), ZoneId.systemDefault());

            if (LocalDateTime.now(ZoneId.systemDefault()).isAfter(expirationTime)) {
                throw new CredentialsExpiredException("Token expired");
            }

            return new JwtAuthenticationToken(signedJWT, null, null);

        } catch (ParseException e) {
            throw new InternalAuthenticationServiceException("Unreadable token");
        } catch (JOSEException e) {
            throw new InternalAuthenticationServiceException("Unreadable signature");
        }*/
    }
}