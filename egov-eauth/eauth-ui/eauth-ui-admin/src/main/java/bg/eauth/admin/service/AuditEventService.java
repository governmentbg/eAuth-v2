package bg.eauth.admin.service;

import bg.bulsi.egov.eauth.audit.actuator.AuditEventConverter;
import bg.bulsi.egov.eauth.audit.model.AuditEventEntity;
import bg.bulsi.egov.eauth.audit.repository.AuditEventEntityRepository;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for managing audit events.
 * <p>
 * This is the default implementation to support SpringBoot Actuator {@code AuditEventRepository}.
 */
@Service
@Transactional
public class AuditEventService {

    private final AuditEventEntityRepository persistenceAuditEventRepository;

    private final AuditEventConverter auditEventConverter;

    public AuditEventService(
    		AuditEventEntityRepository persistenceAuditEventRepository,
        AuditEventConverter auditEventConverter) {

        this.persistenceAuditEventRepository = persistenceAuditEventRepository;
        this.auditEventConverter = auditEventConverter;
    }

    public Page<AuditEventEntity> findAll(Pageable pageable) {
        return persistenceAuditEventRepository.findAll(pageable);
    }

    public Page<AuditEventEntity> findByDates(Instant fromDate, Instant toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditEventDateBetween(fromDate, toDate, pageable);
    }

    public Page<AuditEventEntity> findByFilter(String auditOrigin, String auditEventType, Instant fromDate, Instant toDate, Pageable pageable) {
        return persistenceAuditEventRepository.findAllByAuditOriginAndAuditEventTypeAndAuditEventDateBetween(auditOrigin, auditEventType, fromDate, toDate, pageable);
    }

    public Optional<AuditEventEntity> find(Long id) {
        return Optional.ofNullable(persistenceAuditEventRepository.findById(id))
            .filter(Optional::isPresent)
            .map(Optional::get);
    }
}
