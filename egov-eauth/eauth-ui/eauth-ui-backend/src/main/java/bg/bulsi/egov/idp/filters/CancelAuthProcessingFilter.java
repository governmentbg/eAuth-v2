package bg.bulsi.egov.idp.filters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.SAMLAttribute;
import bg.bulsi.egov.eauth.saml.SAMLPrincipal;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import bg.bulsi.egov.saml.RequestedService;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

@Slf4j
public class CancelAuthProcessingFilter extends AbstractAuthenticationProcessingFilter {
	
	private static final String PROFILE_2FA_SERVICE_ID = "2.16.100.1.1.1.1.13.1.1.1";

	private final SAMLMessageHandler samlMessageHandler;
	private final IdpConfigurationProperties idpConfiguration;
	private final HazelcastService hazelcastService;
	
	public CancelAuthProcessingFilter(String defaultFilterProcessesUrl, SAMLMessageHandler samlMessageHandler,
			IdpConfigurationProperties idpConfiguration, HazelcastService hazelcastService) {
		super(defaultFilterProcessesUrl);
		this.samlMessageHandler = samlMessageHandler;
		this.idpConfiguration = idpConfiguration;
		this.hazelcastService = hazelcastService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		String cancelAuthButton = request.getParameter("cancelAuthButton");
		log.info("cancelAuthButton: [{}]", cancelAuthButton);
		
		// old: throw new InvalidAuthenticationException("Total authentication time expired!"); 
		// or: "Cancel button authentication clicked!"
		String message = (cancelAuthButton == null) ? 
				hazelcastService.get("egov.eauth.sys.mgs.auth.timeout") : hazelcastService.get("egov.eauth.sys.mgs.auth.cancel");
		
		try {
			AuthnRequest authnRequest = getAuthnRequest(request);

			String assertionConsumerServiceURL = idpConfiguration.getAcsEndpoint() != null 
					? idpConfiguration.getAcsEndpoint() : authnRequest.getAssertionConsumerServiceURL();
			
		    if (isProfile2FAServiceProvider(authnRequest)) {
				// 2FA Profile SP cancel authn endpoint
				assertionConsumerServiceURL = loadProfileSPUrl(assertionConsumerServiceURL);
			}
		    log.info("assertionConsumerServiceURL: [{}]", assertionConsumerServiceURL);
		    
			List<SAMLAttribute> attributes = new ArrayList<>();
			
			String nameID = "noIdpSelected"; // no IdP selected
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				log.info("authentication class : [{}]", authentication.getClass().getName());
				
				ExternalIdpUserAuthenticationToken token = (ExternalIdpUserAuthenticationToken) authentication;
				nameID = token.getName();
			}
			log.info("nameID: [{}]", nameID);
			
			SAMLPrincipal principal = new SAMLPrincipal(nameID, NameIDType.UNSPECIFIED,
					attributes, authnRequest.getIssuer().getValue(), authnRequest.getID(), assertionConsumerServiceURL, "");
			samlMessageHandler.sendCancelAuthnResponse(message, principal, response);
		} catch (MessageEncodingException | MessageHandlerException | MarshallingException | SignatureException
				| ComponentInitializationException | EncryptionException | UnmarshallingException | URISyntaxException e) {
			log.error("error: {}", e.getMessage());
		}
		
		return null;
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		log.info("unsuccessfulAuthentication");
		
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString(), failed);
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
			logger.debug("Delegating to authentication failure handler " + this.getFailureHandler());
		}
		this.getRememberMeServices().loginFail(request, response);
		this.getFailureHandler().onAuthenticationFailure(request, response, failed);
	}
	
	private AuthnRequest getAuthnRequest(HttpServletRequest request) throws UnmarshallingException {
	    HttpSession session = request.getSession();

	    Element element = (Element) session.getAttribute("SAMLAuthRequest");
	    if (element == null) {
	      throw new UnmarshallingException("Missing SAML 2.0 AuthRequest");
	    }
	    return (AuthnRequest) Objects
	        .requireNonNull(XMLObjectProviderRegistrySupport
	            .getUnmarshallerFactory().getUnmarshaller(element)).unmarshall(element);
	  }
	
	private boolean isProfile2FAServiceProvider(AuthnRequest authnRequest) {
		RequestedService requestedService = (RequestedService) authnRequest.getExtensions().getUnknownXMLObjects().get(0);
		String serviceID = requestedService.getService().getValue();
		return PROFILE_2FA_SERVICE_ID.equals(serviceID);
	}
	
	private String loadProfileSPUrl(String assertionConsumerServiceURL) throws MalformedURLException, URISyntaxException {
		URL url = new URL(assertionConsumerServiceURL);
		URL profileUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), "/cancel-auth");
		return profileUrl.toString();
	}
}
