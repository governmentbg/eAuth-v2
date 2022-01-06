package bg.bulsi.egov.idp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import bg.bulsi.egov.eauth.metadata.config.model.IdpConfigurationProperties;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.bulsi.egov.idp.client.services.IdpApiClient;
import bg.bulsi.egov.idp.client.services.IdpApiClientTls;
import bg.bulsi.egov.idp.filters.CancelAuthProcessingFailureHandler;
import bg.bulsi.egov.idp.filters.CancelAuthProcessingFilter;
import bg.bulsi.egov.idp.filters.ExternalIdpAuthenticationProcessingFilter;
import bg.bulsi.egov.idp.filters.ExternalIdpAuthenticationSuccessHandler;
import bg.bulsi.egov.idp.filters.ForceAuthnFilter;
import bg.bulsi.egov.idp.filters.IExternalIdpAuthenticationFailureHandler;
import bg.bulsi.egov.idp.filters.IKepFailureHandler;
import bg.bulsi.egov.idp.filters.KepProcessingFilter;
import bg.bulsi.egov.idp.filters.TfaProcessingFilter;
import bg.bulsi.egov.idp.saml.SAMLMessageHandler;
import bg.bulsi.egov.idp.security.EauthLoginUrlAuthenticationEntryPoint;
import bg.bulsi.egov.idp.security.ExtIdpAuthenticationEntryPoint;
import bg.bulsi.egov.idp.security.provider.AuthenticationProvider;
import bg.bulsi.egov.idp.security.provider.ExternalIdpUserAuthenticationProvider;
import bg.bulsi.egov.idp.services.IEidProviderClient;
import bg.bulsi.egov.idp.services.TfaService;
import bg.bulsi.egov.idp.services.temp.ProviderService;
import bg.bulsi.egov.idp.services.temp.UserService;

@EnableWebSecurity(debug = true)
public class WebSecurityConfigurer {
	
	/**
	 * Security Configuration (ApplicationSecurity)
	 */
	@Configuration
	@Order(2)
	public static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

		private final IdpConfigurationProperties idpConfiguration;
		private final SAMLMessageHandler samlMessageHandler;
		private final ProviderService providerService;
		private final ApplicationEventPublisher applicationEventPublisher;

		public ApplicationSecurity(IdpConfigurationProperties idpConfiguration, SAMLMessageHandler samlMessageHandler,
				ProviderService providerService, ApplicationEventPublisher applicationEventPublisher) {
			this.idpConfiguration = idpConfiguration;
			this.samlMessageHandler = samlMessageHandler;
			this.providerService = providerService;
			this.applicationEventPublisher = applicationEventPublisher;
		}

		// ---------------------------------------------------------------------------------------------
		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http
				.antMatcher("/")
				.antMatcher("/favicon.ico")
				.antMatcher("/*.css")
				.antMatcher("/*.js")
				.antMatcher("/login")
				.antMatcher("/error")
				.authorizeRequests()
				.antMatchers("/", "/favicon.ico", "/*.css", "/*.js", "/login", "/error").permitAll();
			
			http
				.antMatcher("/SingleSignOnService")
				.authorizeRequests()
				.antMatchers("/SingleSignOnService").hasRole("USER")
				.antMatchers("/SingleSignOnService").authenticated();
			
			http
				.authorizeRequests().anyRequest().hasRole("USER");

			http
				.exceptionHandling()
				.authenticationEntryPoint(new EauthLoginUrlAuthenticationEntryPoint("/eauth/ssologin"))
				.and()
				.csrf().disable()
				.addFilterBefore(new ForceAuthnFilter(samlMessageHandler, providerService, applicationEventPublisher),
							UsernamePasswordAuthenticationFilter.class)
				.formLogin().disable()
				.httpBasic();

		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) {
			// !!!!!!!!!!!!!!!! МОГО ВАЖНО !!!!!!!!!!!!!!!!!!!
			auth.authenticationProvider(new AuthenticationProvider(idpConfiguration));
		}

		@Override
		public void configure(WebSecurity web) throws Exception {
			super.configure(web);
		}

		/**
		 * @return Current AuthenticationManager
		 * @throws Exception
		 *             -
		 */
		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		// ---------------------------------------------------------------------------------------------
	}

	/***
	 * Втора конфигурация обслужваща пътя /api/idp/**
	 */
	@Configuration
	@Order(1)
	public static class ExternalIdpApiSecurity extends WebSecurityConfigurerAdapter {

		private static final String API_MATCHER = "/api/idp/**";

		private final UserService userService;
		private final ObjectMapper om;
		private final TfaService tfaService;
		@Qualifier("idpApiClient")
		private final IEidProviderClient providerClient;
		@Qualifier("idpApiClientTls")
		private final IEidProviderClient providerTlsClient;

		private final IdpConfigurationProperties idpConfiguration;
		private final SAMLMessageHandler samlMessageHandler;
		private final HazelcastService hazelcastService;
		private final ApplicationEventPublisher applicationEventPublisher;
		
		private final IExternalIdpAuthenticationFailureHandler externalIdpAuthenticationFailureHandler; 
		private final IKepFailureHandler kepFailureHandler; 

		public ExternalIdpApiSecurity(UserService userService, ObjectMapper om, TfaService tfaService,
				IdpApiClient providerClient, IdpApiClientTls providerTlsClient,
				IdpConfigurationProperties idpConfiguration, SAMLMessageHandler samlMessageHandler,
				HazelcastService hazelcastService, ApplicationEventPublisher applicationEventPublisher, 
				IExternalIdpAuthenticationFailureHandler externalIdpAuthenticationFailureHandler, IKepFailureHandler kepFailureHandler) {
			this.userService = userService;
			this.om = om;
			this.tfaService = tfaService;
			this.providerClient = providerClient;
			this.providerTlsClient = providerTlsClient;
			this.idpConfiguration = idpConfiguration;
			this.samlMessageHandler = samlMessageHandler;
			this.hazelcastService = hazelcastService;
			this.applicationEventPublisher = applicationEventPublisher;
			this.externalIdpAuthenticationFailureHandler = externalIdpAuthenticationFailureHandler;
			this.kepFailureHandler = kepFailureHandler;
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) {
			auth.authenticationProvider(new ExternalIdpUserAuthenticationProvider(userService));
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher(API_MATCHER)
				.authorizeRequests()
				.antMatchers("/api/idp/dbreload").permitAll()
				.antMatchers("/api/idp/providers").permitAll()
				.antMatchers("/api/idp/auth-timeout").permitAll()
				.antMatchers("/api/idp/callback/**").permitAll()
				.antMatchers("/api/idp/enabled-otp-methods").permitAll()
				.antMatchers("/napeid**").permitAll()
				.and()
				.authorizeRequests()
				.antMatchers("/api/idp/tfa/**").authenticated().anyRequest().denyAll();

			http
				.csrf().disable()
				.formLogin().disable()
				.logout()
				    .disable() // logout disable?
				.httpBasic().disable()
				.exceptionHandling()
				.authenticationEntryPoint(new ExtIdpAuthenticationEntryPoint());

			http
				.addFilterBefore(authenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(kepProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(tfaProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(cancelAuthProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

		}

		private ExternalIdpAuthenticationProcessingFilter authenticationProcessingFilter() throws Exception {
			ExternalIdpAuthenticationProcessingFilter filter = new ExternalIdpAuthenticationProcessingFilter(om);
			filter.setAuthenticationManager(authenticationManager());
			filter.setAuthenticationSuccessHandler(new ExternalIdpAuthenticationSuccessHandler("/eauth/sso-tfa", providerTlsClient, tfaService));
			filter.setAuthenticationFailureHandler(externalIdpAuthenticationFailureHandler);

			return filter;
		}

		private TfaProcessingFilter tfaProcessingFilter() throws Exception {
			// filter path
			TfaProcessingFilter filter = new TfaProcessingFilter("/api/idp/verify-tfa", tfaService);
			filter.setAuthenticationManager(authenticationManager());
			filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/eauth/sso-tfa?error=2"));

			return filter;
		}
		private KepProcessingFilter kepProcessingFilter() {
			KepProcessingFilter filter = new KepProcessingFilter("/api/idp/qes", userService, applicationEventPublisher);
			filter.setAuthenticationFailureHandler(kepFailureHandler);

			return filter;
		}


		private CancelAuthProcessingFilter cancelAuthProcessingFilter() {
			CancelAuthProcessingFilter filter = new CancelAuthProcessingFilter("/api/idp/cancel-auth", samlMessageHandler, idpConfiguration, hazelcastService);
			filter.setAuthenticationFailureHandler(new CancelAuthProcessingFailureHandler());

			return filter;
		}

	}

}
