package bg.bulsi.egov.eauth.metadata.config.security.tokens;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

public class FederatedUserAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = -5978131412655942333L;

	@Getter
	@Setter
	private Map<String, List<String>> attributes = new HashMap<>();

	
	public FederatedUserAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
	}


	@SuppressWarnings("MethodDoesntCallSuperMethod")
	@Override
	public FederatedUserAuthenticationToken clone() {

		FederatedUserAuthenticationToken clone = new FederatedUserAuthenticationToken(
																						getPrincipal(),
																						getCredentials(),
																						getAuthorities());
		clone.setAttributes(attributes);

		return clone;
	}
}
