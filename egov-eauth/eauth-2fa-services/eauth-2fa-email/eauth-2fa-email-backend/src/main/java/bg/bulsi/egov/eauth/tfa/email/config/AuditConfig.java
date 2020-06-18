package bg.bulsi.egov.eauth.tfa.email.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import bg.bulsi.egov.eauth.audit.config.AuditEventConfig;


@Configuration
@Import(AuditEventConfig.class)
public class AuditConfig {

}
