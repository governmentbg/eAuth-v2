package bg.bulsi.egov.eauth.audit.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bg.bulsi.egov.eauth.audit.model.AuditEventEntity;

/**
 * Spring Data JPA repository for the {@link AuditEventEntity} entity.
 */
@Repository
public interface AuditEventEntityRepository extends JpaRepository<AuditEventEntity, Long> {

    List<AuditEventEntity> findByPrincipal(String principal);

    List<AuditEventEntity> findByAuditEventDateAfter(Instant after);

    List<AuditEventEntity> findByPrincipalAndAuditEventDateAfter(String principal, Instant after);

    List<AuditEventEntity> findByAuditEventDateAfterAndAuditEventType(Instant after, String type);
    
    List<AuditEventEntity> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principal, Instant after, String type);

    Page<AuditEventEntity> findAllByAuditEventDateBetween(Instant fromDate, Instant toDate, Pageable pageable);

    List<AuditEventEntity> findByAuditOriginAndPrincipalAndAuditEventDateAfterAndAuditEventType(String auditOrigin, String principal, Instant after, String type);

    Page<AuditEventEntity> findAllByAuditOriginAndAuditEventTypeAndAuditEventDateBetween(String auditOrigin, String auditEventType,Instant fromDate, Instant toDate, Pageable pageable);

    Page<AuditEventEntity> findAllByAuditOriginAndAuditEventDateBetween(String auditOrigin,Instant fromDate, Instant toDate, Pageable pageable);

    Page<AuditEventEntity> findAllByAuditEventTypeAndAuditEventDateBetween(String auditEventType,Instant fromDate, Instant toDate, Pageable pageable);

}
