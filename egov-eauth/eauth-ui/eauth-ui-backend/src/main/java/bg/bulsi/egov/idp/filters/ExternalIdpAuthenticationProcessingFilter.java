package bg.bulsi.egov.idp.filters;

import bg.bulsi.egov.idp.dto.AuthenticationMap;
import bg.bulsi.egov.idp.dto.LoginRequest;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.var;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class ExternalIdpAuthenticationProcessingFilter extends
    AbstractAuthenticationProcessingFilter {

  private final ObjectMapper om;

  public ExternalIdpAuthenticationProcessingFilter(ObjectMapper om) {
    super(new AntPathRequestMatcher("/api/idp/login", "POST"));
    super.setContinueChainBeforeSuccessfulAuthentication(false);

    this.om = om;
  }

  private static final String AUTHENTICATION_MAP_PREFIX = "__";
  
  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    var map =  request.getParameterMap().entrySet()
        .stream()
        .filter(stringEntry -> stringEntry.getKey().startsWith(AUTHENTICATION_MAP_PREFIX) && stringEntry.getKey().endsWith(AUTHENTICATION_MAP_PREFIX))
        .collect(Collectors.toMap(stringKey -> stringKey.getKey().replace(AUTHENTICATION_MAP_PREFIX, "") , stringEntry -> stringEntry.getValue().length == 1 ? stringEntry.getValue()[0] : ""));

    var providerId = request.getParameter("providerId");

    Authentication token = new ExternalIdpUserAuthenticationToken("idpUser",
        new LoginRequest().providerId(providerId).authMap(new AuthenticationMap(map)),
        AuthorityUtils.createAuthorityList("ROLE_IDP_PRE_AUTHENTICATED"));

    token = this.getAuthenticationManager().authenticate(token);

    return token;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, FilterChain chain, Authentication authResult)
      throws IOException, ServletException {

    if (logger.isDebugEnabled()) {
      logger.debug(
          "Authentication success. Updating SecurityContextHolder to contain: " + authResult);
    }

    // Fire event
    if (this.eventPublisher != null) {
      eventPublisher
          .publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
    }

    getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
  }

  public void setContinueChainBeforeSuccessfulAuthentication(
      boolean continueChainBeforeSuccessfulAuthentication) {
    super.setContinueChainBeforeSuccessfulAuthentication(false);
  }

}
