package bg.bulsi.egov.eauth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.saml2.provider.service.authentication.AuthenticationRequestFactory;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationRequestFactory;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(
        securedEnabled = false,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {


    /**
     * Rest security configuration for /api/
     */
	/*
    @Configuration
    @Order(1)
    public static class RestApiSecurityConfig extends WebSecurityConfigurerAdapter {

        private static final String apiMatcher = "/api/**";

        final JwtAuthenticationProvider authenticationProvider;

        @Autowired
        public RestApiSecurityConfig(JwtAuthenticationProvider authenticationProvider) {
            this.authenticationProvider = authenticationProvider;
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }

        @Override
        public void configure(org.springframework.security.config.annotation.web.builders.WebSecurity web) throws Exception {
            super.configure(web);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http.cors()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .logout().disable()
                    .httpBasic().disable();

            http.antMatcher(apiMatcher).authorizeRequests()
                    .anyRequest()
                    .authenticated();

            http.addFilterBefore(new JwtAuthenticationFilter(apiMatcher, super.authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        }
    }
*/
/*   
	@Configuration
    @Order(1)
    public static class AuthSecurityConfig extends WebSecurityConfigurerAdapter {

        private static final String apiMatcher = "/auth/token/**";

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            http
                    .exceptionHandling()
                    .authenticationEntryPoint(new Http401AuthenticationEntryPoint("eAuth"));

            http.antMatcher(apiMatcher).authorizeRequests()
                    .anyRequest().authenticated();
        }
    }
*/
	// Тук се описват пътищата, които са секюрнати през SAML IdP
	@Configuration
	@Order(1)
	public static class SamlSecurityConfig extends WebSecurityConfigurerAdapter {
		private static final String saml2Matcher = "/**";
		private static final String apiMather = "/api/**";

		@Bean
		public Saml2AuthenticationRequestFactory saml2AuthenticationRequestFactory() {
			return new AuthenticationRequestFactory();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.cors()
			 	.and()
				.csrf().disable()
				.authorizeRequests()
				.antMatchers("/cancel-auth").permitAll()
				.antMatchers(saml2Matcher)
					.authenticated().and().saml2Login();

		}
	}
/*	
	@Configuration
	@Import(EAuthSecurityProviderConfig.class)
	public static class EAuthSecurityConfig {

	}
*/
}
