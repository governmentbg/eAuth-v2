package bg.bulsi.egov.eauth.audit.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;

/**
 * Build AuditApplicationEvent
 *
 * @author gdimitrov
 */
public class EventBuilder {
	
	String principal;
	String type;
	Map<String,Object> data;
	RequestAttributes requestAttributes;
	
	/**
	 * Constructor set by default:
	 * remoteAddress from some attribute headers and http request,
	 * principal from http request.
	 *
	 * @param requestAttributes
	 */
	public EventBuilder(RequestAttributes requestAttributes) {
		this.requestAttributes = requestAttributes;
		this.data = new HashMap<>();
		setDataRemoteAddress();
	}
	
//	public EventBuilder(String principal, RequestAttributes requestAttributes) {
//		this(requestAttributes);
//		this.principal = principal;
//	}
//	
//	public EventBuilder(EventTypes types, RequestAttributes requestAttributes) {
//		this(requestAttributes);
//		this.type = types.name();
//	}
//	
//	public EventBuilder(String principal, EventTypes types, RequestAttributes requestAttributes) {
//		this(requestAttributes);
//		this.principal = principal;
//		this.type = types.name();
//	}
	
	public EventBuilder principal(String principal) {
		this.principal = principal;
		return this;
	}
	
	public EventBuilder type(String type) {
		this.type = type;
		return this;
	}
	
	public EventBuilder type(EventTypes types) {
		type = types.name();
		return this;
	}
	
	public EventBuilder data(String key, Object value) {
		data.put(key, value);
		return this;
	}
	
	public EventBuilder data(DataKeys dataKeys, Object value) {
		data.put(dataKeys.getDataKey(), value);
		return this;
	}

	public EventBuilder data(Map<String,Object> data) {
		this.data.putAll(data);
		return this;
	}
	
	public EventBuilder data(BiConsumer<? super String, ? super Object> action) {
		data.forEach(action);
		return this;
	}
	
	public AuditApplicationEvent build() {
		if (StringUtils.isBlank(principal)) {
			setDefaultPrincipal();
		}
		if (StringUtils.isBlank(type)) {
			setDefaultType();
		}
		return new AuditApplicationEvent(principal, type, data);
	}
	
	private void setDefaultType() {
		type = EventTypes.USER_EVENT.name();
	}

	private void setDefaultPrincipal() {
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		if (request.getUserPrincipal() != null  && StringUtils.isNotBlank(request.getUserPrincipal().getName())) {
			principal = request.getUserPrincipal().getName(); 
		}
	}

	private void setDataRemoteAddress() {
		String remoteAddress = HttpReqRespUtils.getRemoteIP(requestAttributes);
		this.data.put(DataKeys.HTTP_REMOTE_ADDRESS.getDataKey(), remoteAddress);
	}
	
}
