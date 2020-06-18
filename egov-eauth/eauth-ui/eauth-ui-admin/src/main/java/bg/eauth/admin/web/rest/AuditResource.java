package bg.eauth.admin.web.rest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import bg.bulsi.egov.eauth.audit.model.AuditEventEntity;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import bg.eauth.admin.service.AuditEventService;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for getting the {@link AuditEvent}s.
 */
@RestController
@RequestMapping("/management/audits")
public class AuditResource {

    private final AuditEventService auditEventService;

    public AuditResource(AuditEventService auditEventService) {
        this.auditEventService = auditEventService;
    }

    /**
     * {@code GET /audits} : get a page of {@link AuditEvent}s.
     *
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of {@link AuditEvent}s in body.
     */
    @GetMapping
    public ResponseEntity<List<AuditEventEntity>> getAll(@RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder, Pageable pageable) {
        Page<AuditEventEntity> page = auditEventService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET  /audits} : get a page of {@link AuditEvent} between the {@code fromDate} and {@code toDate}.
     *
     * @param fromDate the start of the time period of {@link AuditEvent} to get.
     * @param toDate the end of the time period of {@link AuditEvent} to get.
     * @param queryParams a {@link MultiValueMap} query parameters.
     * @param uriBuilder a {@link UriComponentsBuilder} URI builder.
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of {@link AuditEvent} in body.
     */
    @GetMapping(params = {"fromDate", "toDate"})
    public ResponseEntity<List<AuditEventEntity>> getByDates(
        @RequestParam(value = "fromDate") LocalDate fromDate,
        @RequestParam(value = "toDate") LocalDate toDate,
        @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
        Pageable pageable) {

        Page<AuditEventEntity> page = auditEventService.findByDates(
            fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant(),
            pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping(params = {"auditOrigin", "auditEventType", "fromDate", "toDate"})
    public ResponseEntity<List<AuditEventEntity>> getByFilter(
        @RequestParam(value = "auditOrigin") String auditOrigin,
        @RequestParam(value = "auditEventType") String auditEventType,
        @RequestParam(value = "fromDate") LocalDate fromDate,
        @RequestParam(value = "toDate") LocalDate toDate,
        @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
        Pageable pageable) {

        Page<AuditEventEntity> page = auditEventService.findByFilter(
            auditOrigin,
            auditEventType,
            fromDate.atStartOfDay(ZoneId.systemDefault()).toInstant(),
            toDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1).toInstant(),
            pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(uriBuilder.queryParams(queryParams), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET  /audits/:id} : get an {@link AuditEvent} by id.
     *
     * @param id the id of the entity to get.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the {@link AuditEvent} in body, or status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id:.+}")
    public ResponseEntity<AuditEventEntity> get(@PathVariable Long id) {
        return ResponseUtil.wrapOrNotFound(auditEventService.find(id));
    }
}
