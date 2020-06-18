package bg.bulsi.egov.eauth.audit;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.testng.annotations.Test;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;

@Test(groups = {})
@SpringBootTest(classes = AuditApplication.class)
public class EventTest  extends AbstractTestNGSpringContextTests {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	@Test(groups = {"eventTest","pause"})
	private void testEvent() {
		
		RequestAttributes reqAttr = RequestContextHolder.currentRequestAttributes();
		ServletRequestAttributes servletReqAttr = (ServletRequestAttributes) reqAttr;
		HttpServletRequest request = servletReqAttr.getRequest();
		
		Map<String,Object> data = new HashMap<>();
		data.put(DataKeys.SOURCE.getDataKey(), this.getClass().getName());
		String remoteAddress = HttpReqRespUtils.getRemoteIP(reqAttr);
		data.put(DataKeys.HTTP_REMOTE_ADDRESS.getDataKey(), remoteAddress);
		String principal = null;
		if (request.getUserPrincipal() != null  && StringUtils.isNotBlank(request.getUserPrincipal().getName())) {
			principal = request.getUserPrincipal().getName(); 
		}
		AuditApplicationEvent auditEvent = new AuditApplicationEvent(principal,EventTypes.USER_EVENT.name(),data);
		applicationEventPublisher.publishEvent(auditEvent);	
		
	}
	

	@Test(groups = {"eventTest"})
	private void buildEvent() {
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.USER_EVENT)
				.data(DataKeys.SOURCE, this.getClass().getName())
				.build();
		applicationEventPublisher.publishEvent(auditApplicationEvent);	
	}

}
