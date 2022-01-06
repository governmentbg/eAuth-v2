package bg.bulsi.egov.idp.callback.events;

import org.springframework.context.ApplicationEvent;

import bg.bulsi.egov.eauth.eid.dto.AuthenticationCallbackResult;
import lombok.Getter;

public class CallbackEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private AuthenticationCallbackResult authenticationCallback;

	
	public CallbackEvent(Object source, AuthenticationCallbackResult authenticationCallback) {
		super(source);
		this.authenticationCallback = authenticationCallback;
	}

}
