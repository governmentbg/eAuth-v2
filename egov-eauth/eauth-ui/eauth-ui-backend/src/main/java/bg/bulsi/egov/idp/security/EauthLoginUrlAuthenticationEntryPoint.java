package bg.bulsi.egov.idp.security;


import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.RedirectUrlBuilder;
import org.springframework.security.web.util.UrlUtils;
import org.w3c.dom.Element;

import bg.bulsi.egov.saml.RequestedService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EauthLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

  /**
   * @param loginFormUrl URL where the login page can be found. Should either be relative to the
   *                     web-app context path (include a leading {@code /}) or an absolute URL.
   */
  public EauthLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }

  @Override
  protected String buildRedirectUrlToLoginPage(HttpServletRequest request,
                                               HttpServletResponse response, AuthenticationException authException) {

    String loginForm = determineUrlToUseForThisRequest(request, response,
        authException);

    log.info("loginForm: " + loginForm);
    
    if (UrlUtils.isAbsoluteUrl(loginForm)) {
      return loginForm;
    }

    int serverPort = getPortResolver().getServerPort(request);
    String scheme = request.getScheme();

    RedirectUrlBuilder urlBuilder = new RedirectUrlBuilder();

    urlBuilder.setScheme(scheme);
    urlBuilder.setServerName(request.getServerName());
    urlBuilder.setPort(serverPort);
    urlBuilder.setContextPath(request.getContextPath());
    urlBuilder.setPathInfo(loginForm);
    // urlBuilder.setQuery(getRequestQueryString(request));

    if (isForceHttps() && "http".equals(scheme)) {
      Integer httpsPort = getPortMapper().lookupHttpsPort(serverPort);

      if (httpsPort != null) {
        // Overwrite scheme and port in the redirect URL
        urlBuilder.setScheme("https");
        urlBuilder.setPort(httpsPort);
      } else {
        log.warn("Unable to redirect to HTTPS as no port mapping found for HTTP port "
            + serverPort);
      }
    }

    log.info("urlBuilder.getUrl(): " + urlBuilder.getUrl());
    
    return urlBuilder.getUrl();

  }

  private String getRequestQueryString(HttpServletRequest request)  {
    try {
      AuthnRequest authnRequest = getAuthnRequest(request);

      List<XMLObject> xmlObjects =  authnRequest.getExtensions().getUnknownXMLObjects();
      if(xmlObjects.isEmpty()) return null;
      RequestedService requestedService =       (RequestedService)xmlObjects.get(0);

      return String.format("s=%s&p=%s", requestedService.getService().getValue(),requestedService.getProvider().getValue());

    } catch (UnmarshallingException e) {

      log.debug(e.getMessage());
      return null;
    }
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
}
