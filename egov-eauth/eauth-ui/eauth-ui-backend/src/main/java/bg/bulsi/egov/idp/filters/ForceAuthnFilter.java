package bg.bulsi.egov.idp.filters;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import bg.bulsi.egov.idp.dto.LevelOfAssurance;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * Този филтър премахва SecurityContext Authentication
 */

@Slf4j
public class ForceAuthnFilter extends OncePerRequestFilter {

	private RequestCache requestCache = new HttpSessionRequestCache();

	private SAMLMessageHandler samlMessageHandler;
	private ProviderService providerService;
	private ApplicationEventPublisher applicationEventPublisher;


	public ForceAuthnFilter(SAMLMessageHandler samlMessageHandler, ProviderService providerService, ApplicationEventPublisher applicationEventPublisher) {
		this.samlMessageHandler = samlMessageHandler;
		this.providerService = providerService;
		this.applicationEventPublisher = applicationEventPublisher;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		var servletPath = request.getServletPath();
		HttpSession session = request.getSession();

		var first = requestCache.getMatchingRequest(request, response) == null;

		if (!(servletPath != null && servletPath.endsWith("SingleSignOnService") && first)) {

			chain.doFilter(request, response);

			return;
		}

		SecurityContext securityContext = SecurityContextHolder.getContext();
		var authToken = securityContext.getAuthentication();
		LevelOfAssurance principalLoa = LevelOfAssurance.LOW;

		if (authToken instanceof ExternalIdpUserAuthenticationToken) {
			securityContext.setAuthentication(null);
		} else if (authToken instanceof FederatedUserAuthenticationToken) {
			FederatedUserAuthenticationToken token = (FederatedUserAuthenticationToken) authToken;
			IdpPrincipal princiapl = (IdpPrincipal) token.getPrincipal();
			principalLoa = princiapl.getLoa();
			log.debug("principalLoa: [{}]", principalLoa);
		}

		try {
			AuthnRequest authnRequest = getAuthnRequest(request, response, session);
			LevelOfAssurance authnRequestLoa = providerService.getLevelOfAssurance(authnRequest);
			log.debug("authnRequestLoa: [{}]", authnRequestLoa);

			if (authnRequest.isForceAuthn() || authnRequestLoa.ordinal() > principalLoa.ordinal()) {
				securityContext.setAuthentication(null);
			}

			chain.doFilter(request, response);

		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}


	private AuthnRequest getAuthnRequest(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws MessageDecodingException, ComponentInitializationException, MessageHandlerException,
			SignatureException, MarshallingException {
		MessageContext<SAMLObject> messageContext = samlMessageHandler.extractSAMLMessageContext(request, response,
				request.getMethod().equalsIgnoreCase("POST"));

		AuthnRequest authnRequest = (AuthnRequest) messageContext.getMessage();

		final MarshallerFactory marshallerFactory = XMLObjectProviderRegistrySupport.getMarshallerFactory();
		Element element = Objects.requireNonNull(marshallerFactory.getMarshaller(Objects.requireNonNull(authnRequest)))
				.marshall(authnRequest);

		session.setAttribute("SAMLAuthRequest", element);
		log.debug("Add a SAMLAuthRequest to the session");

		/*
		 * AuditEvent
		 */
		{ //TODO remove test
			log.info("AuthnRequest getAssertionConsumerServiceURL {}", authnRequest.getConsent());
			log.info("AuthnRequest getDestination {}", authnRequest.getDestination());
			log.info("AuthnRequest getID {}", authnRequest.getID());
			log.info("AuthnRequest getProviderName {}", authnRequest.getProviderName());
			log.info("AuthnRequest getAssertionConsumerServiceURL {}", authnRequest.getAssertionConsumerServiceURL());
		}
		String principal = null;
		if (authnRequest.getIssuer() != null) {
			principal = authnRequest.getIssuer().getValue();
		}
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.principal(principal)
				.type(EventTypes.SAML_AUTHENTICATION_REQUEST)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);

		return authnRequest;
	}

}
