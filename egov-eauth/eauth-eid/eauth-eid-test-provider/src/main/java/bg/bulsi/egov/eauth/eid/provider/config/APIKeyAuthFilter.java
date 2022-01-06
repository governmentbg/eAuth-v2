package bg.bulsi.egov.eauth.eid.provider.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

public class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String headerName;

    public APIKeyAuthFilter(String headerName) {
        this.headerName = headerName;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
    	return request.getHeader(headerName);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

}