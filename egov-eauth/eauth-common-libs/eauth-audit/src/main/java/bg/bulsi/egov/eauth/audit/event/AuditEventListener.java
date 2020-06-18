package bg.bulsi.egov.eauth.audit.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.listener.AbstractAuditListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditEventListener extends AbstractAuditListener {

	@Autowired
	private AuditEventRepository auditEventRepository;


	@Override
	protected void onAuditEvent(AuditEvent event) {

		log.info("On audit event listener: timestamp: {}, principal: {}, type: {}, data: {}",
				event.getTimestamp(),
				event.getPrincipal(),
				event.getType(),
				event.getData());

		auditEventRepository.add(event);
	}
}
