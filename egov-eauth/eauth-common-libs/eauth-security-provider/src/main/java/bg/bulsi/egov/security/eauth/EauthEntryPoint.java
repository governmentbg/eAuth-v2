package bg.bulsi.egov.security.eauth;

import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class EauthEntryPoint extends GenericFilterBean implements AuthenticationEntryPoint {

    private static final String FILTER_URL = "/eaft/login";

    @Autowired
    protected EauthProviderProperties properties;

    @Autowired
    protected ViewResolver viewResolver;

    private boolean processFilter(HttpServletRequest request) {

        String filterUrl = StringUtils.isEmpty(properties.getEauthEntryPointPath()) ? FILTER_URL : properties.getEauthEntryPointPath().trim();
        return (request.getRequestURI().contains(filterUrl));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        FilterInvocation fi = new FilterInvocation(request, response, chain);
        if (!processFilter(fi.getRequest())) {
            chain.doFilter(request, response);
            return;
        }
        if(!"GET".equals(  fi.getHttpRequest().getMethod().toUpperCase())) {
            throw new HttpRequestMethodNotSupportedException(fi.getHttpRequest().getMethod());
        }

        commence(fi.getRequest(), fi.getResponse(), null);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // POST binding
        try {
            View v = viewResolver.resolveViewName("post-binding", Locale.ENGLISH);

            Map<String, String> model = new HashMap<>();
            model.put("action", properties.getAuthenticationResponceUrl());
            model.put("RelayState", "alabala");

            assert v != null;
            v.render(model, request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }


        // Redirect binding
       /* response.setStatus(SC_FOUND);
        response.setHeader("Cache-control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        request.setCharacterEncoding("UTF-8");

        // Към страницата на провайдера
        response.sendRedirect(properties.getAuthenticationResponceUrl());*/
    }

}
