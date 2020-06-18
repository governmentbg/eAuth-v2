package bg.eauth.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.transaction.annotation.Transactional;

import bg.bulsi.egov.eauth.audit.actuator.AuditEventConverter;
import bg.bulsi.egov.eauth.audit.actuator.CustomAuditEventRepository;
import bg.bulsi.egov.eauth.audit.model.AuditEventEntity;
import bg.bulsi.egov.eauth.audit.repository.AuditEventEntityRepository;
import bg.eauth.admin.EauthAdminApp;
import bg.eauth.admin.config.Constants;

/**
 * Integration tests for {@link CustomAuditEventRepository}.
 */
@SpringBootTest(classes = EauthAdminApp.class)
@Transactional
public class CustomAuditEventRepositoryIT {
    protected static final int EVENT_DATA_COLUMN_MAX_LENGTH = 255;

    @Autowired
    private AuditEventEntityRepository persistenceAuditEventRepository;

    @Autowired
    private AuditEventConverter auditEventConverter;

    private CustomAuditEventRepository customAuditEventRepository;

    private AuditEventEntity testUserEvent;

    private AuditEventEntity testOtherUserEvent;

    private AuditEventEntity testOldUserEvent;

    @BeforeEach
    public void setup() {
        customAuditEventRepository = new CustomAuditEventRepository(persistenceAuditEventRepository, auditEventConverter);
        persistenceAuditEventRepository.deleteAll();
        Instant oneHourAgo = Instant.now().minusSeconds(3600);

        testUserEvent = new AuditEventEntity("application","test-user",oneHourAgo,"test-type");
//        testUserEvent.setAuditOrigin("application");
//        testUserEvent.setPrincipal("test-user");
//        testUserEvent.setAuditEventDate(oneHourAgo);
//        testUserEvent.setAuditEventType("test-type");
        Map<String, String> data = new HashMap<>();
        data.put("test-key", "test-value");
        testUserEvent.setData(data);

        testOldUserEvent = new AuditEventEntity("application","test-user",oneHourAgo.minusSeconds(10000),"test-type");
//        testOldUserEvent.setPrincipal("test-user");
//        testOldUserEvent.setAuditEventDate(oneHourAgo.minusSeconds(10000));
//        testOldUserEvent.setAuditEventType("test-type");

        testOtherUserEvent = new AuditEventEntity("application","other-test-user",oneHourAgo,"test-type");
//        testOtherUserEvent.setPrincipal("other-test-user");
//        testOtherUserEvent.setAuditEventDate(oneHourAgo);
//        testOtherUserEvent.setAuditEventType("test-type");
    }

    @Test
    public void addAuditEvent() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        AuditEventEntity persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(persistentAuditEvent.getAuditEventType()).isEqualTo(event.getType());
        assertThat(persistentAuditEvent.getData()).containsKey("test-key");
        assertThat(persistentAuditEvent.getData().get("test-key")).isEqualTo("test-value");
        assertThat(persistentAuditEvent.getAuditEventDate().truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(event.getTimestamp().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    public void addAuditEventTruncateLargeData() {
        Map<String, Object> data = new HashMap<>();
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < EVENT_DATA_COLUMN_MAX_LENGTH + 10; i++) {
            largeData.append("a");
        }
        data.put("test-key", largeData);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        AuditEventEntity persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getPrincipal()).isEqualTo(event.getPrincipal());
        assertThat(persistentAuditEvent.getAuditEventType()).isEqualTo(event.getType());
        assertThat(persistentAuditEvent.getData()).containsKey("test-key");
        String actualData = persistentAuditEvent.getData().get("test-key");
        assertThat(actualData.length()).isEqualTo(EVENT_DATA_COLUMN_MAX_LENGTH);
        assertThat(actualData).isSubstringOf(largeData);
        assertThat(persistentAuditEvent.getAuditEventDate().truncatedTo(ChronoUnit.MILLIS))
            .isEqualTo(event.getTimestamp().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    public void testAddEventWithWebAuthenticationDetails() {
        HttpSession session = new MockHttpSession(null, "test-session-id");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        request.setRemoteAddr("1.2.3.4");
        WebAuthenticationDetails details = new WebAuthenticationDetails(request);
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", details);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        AuditEventEntity persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getData().get("remoteAddress")).isEqualTo("1.2.3.4");
        assertThat(persistentAuditEvent.getData().get("sessionId")).isEqualTo("test-session-id");
    }

    @Test
    public void testAddEventWithNullData() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", null);
        AuditEvent event = new AuditEvent("test-user", "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(1);
        AuditEventEntity persistentAuditEvent = persistentAuditEvents.get(0);
        assertThat(persistentAuditEvent.getData().get("test-key")).isEqualTo("null");
    }

    @Test
    public void addAuditEventWithAnonymousUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent(Constants.ANONYMOUS_USER, "test-type", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(0);
    }

    @Test
    public void addAuditEventWithAuthorizationFailureType() {
        Map<String, Object> data = new HashMap<>();
        data.put("test-key", "test-value");
        AuditEvent event = new AuditEvent("test-user", "AUTHORIZATION_FAILURE", data);
        customAuditEventRepository.add(event);
        List<AuditEventEntity> persistentAuditEvents = persistenceAuditEventRepository.findAll();
        assertThat(persistentAuditEvents).hasSize(0);
    }

}
