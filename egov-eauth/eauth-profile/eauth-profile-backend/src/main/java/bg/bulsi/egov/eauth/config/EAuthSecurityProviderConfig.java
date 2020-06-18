package bg.bulsi.egov.eauth.config;

import bg.bulsi.egov.security.eauth.*;
import bg.bulsi.egov.security.eauth.config.EauthProviderProperties;
import bg.bulsi.egov.security.eauth.userdetails.EauthUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableConfigurationProperties(EauthProviderProperties.class)
public class EAuthSecurityProviderConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private EauthProviderProperties eauthProviderProperties;

    @Bean
    public UserDetailsService userDetailsService() {
        return new EauthUserDetailService();
    }

    @Bean
    public FilterChainProxy customFilter() throws Exception {
        List<SecurityFilterChain> chains = new ArrayList<>();

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/eaft/authorize/**"),
                customEntryPoint()));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/eaft/callback/**"),
                receiverFilter()));

        return new FilterChainProxy(chains);
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successRedirectHandler() {
        EauthSuccessRedirectHandler target = new EauthSuccessRedirectHandler();
        target.setDefaultTargetUrl(eauthProviderProperties.getRedirect());
        return target;
    }

    @Bean
    public SimpleUrlAuthenticationFailureHandler authenticationFailureHandler() {
        return new EauthAuthenticationFailureHandler(eauthProviderProperties.getRedirectToError());
    }

    @Bean
    public EauthProcessingFilter receiverFilter() throws Exception {

        EauthProcessingFilter filter = new EauthProcessingFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationSuccessHandler(successRedirectHandler());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler());

        return filter;
    }

    @Bean
    public EauthEntryPoint customEntryPoint() {
        return new EauthEntryPoint();
    }

    @Bean
    public EauthAuthenticationProvider customAuthenticationProvider() {
        return new EauthAuthenticationProvider(userDetailsService());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(customAuthenticationProvider());
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .exceptionHandling()
                .authenticationEntryPoint(customEntryPoint());
        http
                .csrf()
                .disable();

        http.addFilterAfter(customFilter(), BasicAuthenticationFilter.class);

        http
                .authorizeRequests()
                .antMatchers("/error").permitAll()
                .antMatchers("/eaft/**").permitAll()
                .anyRequest().authenticated();

    }
}