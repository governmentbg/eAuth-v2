package bg.bulsi.egov.security.eauth;

import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import bg.bulsi.egov.security.eauth.userdetails.UserPrinciple;
import bg.bulsi.egov.security.utils.PersonalIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class EauthProcessingFilter extends AbstractAuthenticationProcessingFilter {

    //-------------- САМО ЗА ТЕСТОВЕТЕ !!! --------------

    @Nullable
    protected String obtainData(HttpServletRequest request) {
        return request.getParameter("SAMLResponse");
    }

    //--------------

    public static final String FILTER_URL = "/eaft/receiver";
    @Autowired
    protected EauthProviderProperties properties;
    private String filterProcessesUrl;

    public EauthProcessingFilter() {
        this(FILTER_URL);
    }

    protected EauthProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setFilterProcessesUrl(defaultFilterProcessesUrl);
    }

    private static boolean processFilter(String filterName, HttpServletRequest request) {
        return (request.getRequestURI().contains(filterName));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        if(!"POST".equals(        request.getMethod().toUpperCase())) {
            log.debug("Unsupported method " + request.getMethod() + " for path " + filterProcessesUrl + " ### ");
            throw new HttpRequestMethodNotSupportedException(request.getMethod());
        }

        log.debug("Attempting authentication using profile");

        //-------------- САМО ЗА ТЕСТОВЕТЕ !!! --------------
        String username = obtainData(request);

        if (username == null) {
            username = "";
        }
        username = username.trim();
        //--------------

        if(username.length() == 0) throw new BadCredentialsException("Missing SAMLResponse data!");

        // TODO --- ?????? ----
        EauthAuthenticationToken token = new EauthAuthenticationToken(
                new UserPrinciple(PersonalIdUtils.encrypt(username, properties.getIdSecret())), null);

        try {
            log.debug(String.format( "User %s authenticated", username));

            return getAuthenticationManager().authenticate(token);
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Incoming message is invalid", ex);
        }

    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return processFilter(getFilterProcessesUrl(), request);
    }

    /**
     * Gets the URL used to determine if this Filter is invoked
     *
     * @return the URL used to determine if this Filter is invoked
     */
    private String getFilterProcessesUrl() {
        return filterProcessesUrl;
    }

    @Override
    @Value("${eauth.security.provider.responce-receiver-path:" + FILTER_URL + "}")
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
        super.setFilterProcessesUrl(filterProcessesUrl);
    }
}
