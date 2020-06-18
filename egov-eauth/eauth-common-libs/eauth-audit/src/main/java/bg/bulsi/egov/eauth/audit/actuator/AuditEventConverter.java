package bg.bulsi.egov.eauth.audit.actuator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.AuditEventEntity;
import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.util.HttpReqRespUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditEventConverter {
	
	/**
     * Should be the same as in Liquibase migration.
     */
    protected static final int EVENT_DATA_COLUMN_MAX_LENGTH = 255;
    
	@Autowired
	private ApplicationContext applicationContext;
    /**
     * Convert a list of {@link AuditEvent }s to a list of {@link AuditEventEntity}s.
     *
     * @param auditEvents the list to convert.
     * @return the converted list.
     */
    public List<AuditEventEntity> convertToAuditEntity(Iterable<AuditEvent> auditEvents) {
        if (auditEvents == null) {
            return Collections.emptyList();
        }
        List<AuditEventEntity> persistAuditEvents = new ArrayList<>();
        for (AuditEvent tAuditEvent : auditEvents) {
        	persistAuditEvents.add(convertToAuditEntity(tAuditEvent));
        }
        return persistAuditEvents;
    }

    /**
     * Convert a {@link AuditEventEntity} to an {@link AuditEvent}.
     *
     * @param auditEvent the event to convert.
     * @return the converted list.
     */
    public AuditEventEntity convertToAuditEntity(AuditEvent auditEvent) {
    	
        if (auditEvent == null) {
            return null;
        }
        String springContext = applicationContext.getId();
        
        log.debug("User Spring Context for audit logging, dispalayName='{}', Id='{}'  ",applicationContext.getDisplayName(),applicationContext.getId());
        
        AuditEventEntity auditEventEntity = new AuditEventEntity(springContext,auditEvent.getPrincipal(),auditEvent.getTimestamp(),auditEvent.getType());
        
        if (auditEvent.getData() !=null && !auditEvent.getData().isEmpty()) {
        	
        	auditEventEntity.setData(convertDataToStrings(auditEvent.getData()));
			
        	for (Entry<String, String> entry: auditEventEntity.getData().entrySet()) {				
				if (DataKeys.HTTP_REMOTE_ADDRESS.getDataKey().equals(entry.getKey())) {					
					auditEventEntity.setClientIP(entry.getValue());
				}
			}
		}
        
        if (auditEventEntity.getClientIP() == null) {
        	String remoteAddr = HttpReqRespUtils.getRemoteIP(RequestContextHolder.currentRequestAttributes());
//        	auditEventEntity.getData().put(DataKeys.HTTP_REMOTE_ADDRESS.getDataKey(), remoteAddr);
        	auditEventEntity.setClientIP(remoteAddr);
		}
        
        return auditEventEntity;
    }

    /**
     * Convert a list of {@link PersistentAuditEvent}s to a list of {@link AuditEvent}s.
     *
     * @param persistentAuditEvents the list to convert.
     * @return the converted list.
     */
    public List<AuditEvent> convertToAuditEvent(Iterable<AuditEventEntity> persistentAuditEvents) {
        if (persistentAuditEvents == null) {
            return Collections.emptyList();
        }
        List<AuditEvent> auditEvents = new ArrayList<>();
        for (AuditEventEntity persistentAuditEvent : persistentAuditEvents) {
            auditEvents.add(convertToAuditEvent(persistentAuditEvent));
        }
        return auditEvents;
    }

    /**
     * Convert a {@link PersistentAuditEvent} to an {@link AuditEvent}.
     *
     * @param persistentAuditEvent the event to convert.
     * @return the converted list.
     */
    public AuditEvent convertToAuditEvent(AuditEventEntity persistentAuditEvent) {
        if (persistentAuditEvent == null) {
            return null;
        }
        return new AuditEvent(persistentAuditEvent.getAuditEventDate(), persistentAuditEvent.getPrincipal(),
            persistentAuditEvent.getAuditEventType(), convertDataToObjects(persistentAuditEvent.getData()));
    }

    /**
     * Internal conversion. This is needed to support the current SpringBoot actuator {@code AuditEventRepository} interface.
     *
     * @param data the data to convert.
     * @return a map of {@link String}, {@link Object}.
     */
    public Map<String, Object> convertDataToObjects(Map<String, String> data) {
        Map<String, Object> results = new HashMap<>();

        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }

    /**
     * Internal conversion. This method will allow to save additional data.
     * By default, it will save the object as string.
     *
     * @param data the data to convert.
     * @return a map of {@link String}, {@link String}.
     */
    public Map<String, String> convertDataToStrings(Map<String, Object> data) {
        Map<String, String> results = new HashMap<>();

        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                // Extract the data that will be saved.
                if (entry.getValue() instanceof WebAuthenticationDetails) {
                    WebAuthenticationDetails authenticationDetails = (WebAuthenticationDetails) entry.getValue();
                    results.put(DataKeys.HTTP_REMOTE_ADDRESS.getDataKey(), authenticationDetails.getRemoteAddress());
                    results.put(DataKeys.HTTP_SESSION_ID.getDataKey(), authenticationDetails.getSessionId());
                } else {
                    results.put(entry.getKey(), Objects.toString(entry.getValue()));
                }
                
            }
        }
        return truncate(results);
    }
    /**
     * Truncate event data that might exceed column length.
     */
    private Map<String, String> truncate(Map<String, String> data) {
        Map<String, String> results = new HashMap<>();

        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String value = entry.getValue();
                if (value != null) {
                    int length = value.length();
                    if (length > EVENT_DATA_COLUMN_MAX_LENGTH) {
                        value = value.substring(0, EVENT_DATA_COLUMN_MAX_LENGTH);
                        log.warn("Event data for {} too long ({}) has been truncated to {}. Consider increasing column width.",
                                 entry.getKey(), length, EVENT_DATA_COLUMN_MAX_LENGTH);
                    }
                }
                results.put(entry.getKey(), value);
            }
        }
        return results;
    }
}
