package bg.bulsi.egov.idp.controlers;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.SAMLException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.eauth.saml.SAMLAttribute;
import bg.bulsi.egov.eauth.saml.SAMLPrincipal;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.security.IdpPrincipal;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

@Slf4j
@Controller
public class SsoController {

  // TODO check it!!!
  private static final String PATH = "eauth-common-libs/egov-saml-impl/src/main/resources/";
  private static final String FILE_METADATA_XML = PATH + "test_metadata.xml";

  private final SAMLMessageHandler samlMessageHandler;
  private final IdpConfigurationProperties idpConfiguration;

  public SsoController(
      SAMLMessageHandler samlMessageHandler, IdpConfigurationProperties idpConfiguration) {
    this.samlMessageHandler = samlMessageHandler;
    this.idpConfiguration = idpConfiguration;
  }

  //TODO remove!!!
  @ResponseBody
  @GetMapping("/metadata")
  public ResponseEntity<Object> getSAMLMetadata() {

    ResponseEntity<Object> response;

    try (FileInputStream xmlMetadataFs = new FileInputStream(FILE_METADATA_XML)) {
      // Create a factory
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Use the factory to create a builder
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(xmlMetadataFs);

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource source = new DOMSource(doc);
      response = ResponseEntity.ok().body(source);
    } catch (FileNotFoundException e) {
      log.error("FILE_METADATA_XML: {}", e.getMessage());
      response = ResponseEntity.notFound().build();
    } catch (Exception ex) {
      response = ResponseEntity.status(500).build();
    }

    return response;
  }

  // GET Binding
  @GetMapping("/SingleSignOnService")
  public void singleSignOnServiceGet(HttpServletRequest request, HttpServletResponse response,
                                     Authentication authentication)
      throws MarshallingException, SignatureException, MessageEncodingException,
          SecurityException, ComponentInitializationException, MessageHandlerException, EncryptionException, SAMLException {

    doSSO(request, response, authentication);
  }

  // POST Binding
  @PostMapping("/SingleSignOnService")
  public void singleSignOnServicePost(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication)
      throws MarshallingException, SignatureException, MessageEncodingException,
          SecurityException, ComponentInitializationException, MessageHandlerException, EncryptionException, SAMLException {

    doSSO(request, response, authentication);
  }

  /**
   * @param request        HTTP request
   * @param response       HTTP response
   * @param authentication - намира се аутентикирания потребител през системата (ТОВА ТРЯБВА ДА ВИДЯ
   *                       ДЕЛИ НЕ МОЖЕ ПРЕЗ UserDetailService)
   * @throws SecurityException        2
   * @throws MarshallingException     4
   * @throws SignatureException       5
   * @throws MessageEncodingException 6
   */
  private void doSSO(HttpServletRequest request, HttpServletResponse response,
                     Authentication authentication)
      throws MarshallingException, SignatureException,
      MessageEncodingException, SecurityException, ComponentInitializationException, MessageHandlerException, EncryptionException, SAMLException {

    try {
      AuthnRequest authnRequest = getAuthnRequest(request);

      String assertionConsumerServiceURL = idpConfiguration.getAcsEndpoint() != null ?
          idpConfiguration.getAcsEndpoint() :
          authnRequest.getAssertionConsumerServiceURL();

      List<SAMLAttribute> attributes = attributes(authentication);

      SAMLPrincipal principal = new SAMLPrincipal(authentication.getName(),
          attributes.stream()
              .filter(attr -> "urn:oasis:names:tc:SAML:1.1:nameid-format".equals(attr.getName()))
              .findFirst()
              .map(SAMLAttribute::getValue).orElse(NameIDType.UNSPECIFIED),
          attributes, authnRequest.getIssuer().getValue(),
          authnRequest.getID(), assertionConsumerServiceURL, "");

      samlMessageHandler.sendAuthnResponse(principal, response);

    } catch (UnmarshallingException e) {
      throw new SAMLException(e);
    }

  }

  private AuthnRequest getAuthnRequest(HttpServletRequest request) throws UnmarshallingException {
    HttpSession session = request.getSession();

    Element element = (Element) session.getAttribute("SAMLAuthRequest");
    if (element == null) {
      throw new UnmarshallingException("Missing SAML 2.0 AuthRequest");
    }

    AuthnRequest authnRequest = (AuthnRequest) Objects
        .requireNonNull(XMLObjectProviderRegistrySupport
            .getUnmarshallerFactory().getUnmarshaller(element)).unmarshall(element);

    log.debug("Remove a SAMLAuthRequest from the session");
    session.removeAttribute("SAMLAuthRequest");
    return authnRequest;
  }

  private List<SAMLAttribute> attributes(Authentication authentication) {

    IdpPrincipal principal = (IdpPrincipal)authentication.getPrincipal();

    return principal.getAttributes().stream().map(identityAttributes -> new SAMLAttribute(
        identityAttributes.getUrn(),  singletonList(identityAttributes.getValue())
    )).collect(toList());
  }

  
  @GetMapping("/logout") 
	public String logout(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		log.info("do logout!");

		logout();
		
		return "logout";
	}
  
  private void logout() {
      // Logout
      ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
      HttpSession session = attr.getRequest().getSession(false);
      if (session != null) {
    	  session.removeAttribute("SAMLAuthRequest");
    	  session.invalidate();
      }

      SecurityContextHolder.getContext().setAuthentication(null);
      SecurityContextHolder.clearContext();
  }
}
