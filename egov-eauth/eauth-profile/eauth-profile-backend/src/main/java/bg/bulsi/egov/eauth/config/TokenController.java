package bg.bulsi.egov.eauth.config;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.xml.sax.SAXException;

import bg.bulsi.egov.eauth.services.Saml2Service;
import bg.bulsi.egov.security.eauth.userdetails.EauthUserPrincipal;
import bg.bulsi.egov.security.jwt.JwtProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class TokenController {

	@Autowired
	private Saml2Service service;
	
	private final JwtProvider jwtProvider;

    public TokenController(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @GetMapping("/auth/token")
    @ResponseBody
    public ResponseEntity token() throws ParserConfigurationException, SAXException, IOException, UnmarshallingException {
    	log.info("TOKEN controller !!!!");
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	log.info("authentication: [{}]", authentication);
        String nid = null;
        String name = null;
    	if (authentication instanceof EauthUserPrincipal) {
    		EauthUserPrincipal userPrincipal = (EauthUserPrincipal) authentication.getPrincipal();
    		nid = userPrincipal.getId();
    	} else if (authentication instanceof Saml2Authentication) {
    		Saml2Authentication saml2Auth = (Saml2Authentication) authentication;
    		Response saml2Response = service.getSaml2Response(saml2Auth.getSaml2Response());
    		nid = service.getNidFromSaml2Response(saml2Response);
    		name = service.getNameFromSaml2Response(saml2Response);
    	} else {
    		log.error("Wrong Authentication type: [{}]", authentication.getClass().getCanonicalName());
    	}
    	
    	log.info("authenticated nid: [{}]", nid);
    	String encryptedNid = service.encrypted(nid);
    	log.info("authenticated nid: [{}]", encryptedNid);
        
    	log.info("authenticated name: [{}]", name);
    	
    	String token = jwtProvider.createToken(encryptedNid, name);

        ///logout();

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_PLAIN)
                .body(token);
    }

    private void logout() {
        // Logout
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attr.getRequest().getSession(false).invalidate();

        SecurityContextHolder.getContext().setAuthentication(null);
        SecurityContextHolder.clearContext();
    }
}