package bg.bulsi.egov.idp.filters;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import bg.bulsi.egov.eauth.metadata.config.security.tokens.FederatedUserAuthenticationToken;
import bg.bulsi.egov.idp.dto.CodeData;
import bg.bulsi.egov.idp.dto.OTPMethod;
import bg.bulsi.egov.idp.security.InvalidAuthenticationException;
import bg.bulsi.egov.idp.security.tokens.ExternalIdpUserAuthenticationToken;
import bg.bulsi.egov.idp.services.TfaService;
import lombok.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TfaProcessingFilter extends AbstractAuthenticationProcessingFilter {

	private final TfaService tfaService;

	public TfaProcessingFilter(String defaultFilterProcessesUrl, TfaService tfaService) {
		super(defaultFilterProcessesUrl);
		this.tfaService = tfaService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (!(authentication instanceof ExternalIdpUserAuthenticationToken)) {
			throw new AuthenticationCredentialsNotFoundException("Missing authenticated user");
		}

		// TODO: валидиране на пропъртитата
		final String tId = request.getParameter("tId");
		final String code = request.getParameter("code");
		final String method = request.getParameter("method");

		log.info("tId: [{}]; code: [{}]; method: [{}]", tId, code, method);

		var data = new CodeData();
		data.setTId(tId);
		data.setCode(code);
		if (StringUtils.isNotEmpty(method)) {
			data.setMethod(OTPMethod.valueOf(method));
		} else {
			throw new InvalidAuthenticationException("Не е изпратен код!");
		}

		var status = this.tfaService.validate(data);

		if (Boolean.FALSE.equals(status.valid())) {
			throw new InvalidAuthenticationException(status.message());
		}

		FederatedUserAuthenticationToken token = new FederatedUserAuthenticationToken(authentication.getPrincipal(),
				null, AuthorityUtils.createAuthorityList("ROLE_FULL_AUTHENTICATED"));
		token.setDetails(authentication.getDetails());

		return token;
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString(), failed);
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
			logger.debug("Delegating to authentication failure handler " + this.getFailureHandler());
		}
		this.getRememberMeServices().loginFail(request, response);
		this.getFailureHandler().onAuthenticationFailure(request, response, failed);
	}

}
