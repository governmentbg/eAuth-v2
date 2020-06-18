package bg.eauth.admin.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import bg.bulsi.egov.eauth.audit.model.DataKeys;
import bg.bulsi.egov.eauth.audit.model.EventTypes;
import bg.bulsi.egov.eauth.audit.util.EventBuilder;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import bg.eauth.admin.domain.Config;
import bg.eauth.admin.repository.ConfigRepository;
import bg.eauth.admin.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link bg.eauth.admin.domain.Config}.
 */
@RestController
@RequestMapping("/api")
public class ConfigResource {

    @Autowired
    private HazelcastService hazelcastService;

    @Autowired
    private RestTemplate restTemplate;
    
	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

    private final Logger log = LoggerFactory.getLogger(ConfigResource.class);

    private static final String ENTITY_NAME = "config";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConfigRepository configRepository;

    public ConfigResource(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    /**
     * {@code POST  /configs} : Create a new config.
     *
     * @param config the config to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new config, or with status {@code 400 (Bad Request)} if the config has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/configs")
    public ResponseEntity<Config> createConfig(@RequestBody Config config) throws URISyntaxException {

		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
    	
        log.debug("REST request to save Config : {}", config);
        if (config.getPropertyKey() != null) {
            throw new BadRequestAlertException("A new config cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Config result = configRepository.save(config);

        hazelcastService.put(config.getPropertyKey(), config.getPropertyValue());
        reloadUIBackendProps();
        
        /*
         * AuditEvent
         */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.ADD_CONFIGURATION)
				.data(DataKeys.SOURCE, this.getClass().getName())
		        .data(DataKeys.DB_MODEL, result.getPropertyKey())
				.build();
        applicationEventPublisher.publishEvent(auditApplicationEvent);	

        return ResponseEntity.created(new URI("/api/configs/" + result.getPropertyKey()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getPropertyKey()))
            .body(result);
    }

    /**
     * {@code PUT  /configs} : Updates an existing config.
     *
     * @param config the config to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated config,
     * or with status {@code 400 (Bad Request)} if the config is not valid,
     * or with status {@code 500 (Internal Server Error)} if the config couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/configs")
    public ResponseEntity<Config> updateConfig(@RequestBody Config config) throws URISyntaxException {
    	
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		
        log.debug("REST request to update Config : {}", config);
        if (config.getPropertyKey() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Config result = configRepository.save(config);

        hazelcastService.put(config.getPropertyKey(), config.getPropertyValue());
        reloadUIBackendProps();
        
        /*
         * AuditEvent
         */
		AuditApplicationEvent auditApplicationEvent = new EventBuilder(RequestContextHolder.currentRequestAttributes())
				.type(EventTypes.CHANGE_CONFIGURATION)
				.data(DataKeys.SOURCE, this.getClass().getName())
		        .data(DataKeys.DB_MODEL, result.getPropertyKey())
				.build();
        applicationEventPublisher.publishEvent(auditApplicationEvent);	

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, config.getPropertyKey()))
            .body(result);
    }

    /**
     * {@code GET  /configs} : get all the configs.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of configs in body.
     */
    @GetMapping("/configs")
    public List<Config> getAllConfigs() {
        log.debug("REST request to get all Configs");
        return configRepository.findAll(Sort.by("propertyKey"));
    }

    /**
     * {@code GET  /configs/:id} : get the "id" config.
     *
     * @param id the id of the config to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the config, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/configs/{id}")
    public ResponseEntity<Config> getConfig(@PathVariable String id) {
        log.debug("REST request to get Config : {}", id);
        Optional<Config> config = configRepository.findByPropertyKey(id);

        return ResponseUtil.wrapOrNotFound(config);
    }

    /**
     * {@code DELETE  /configs/:id} : delete the "id" config.
     *
     * @param id the id of the config to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String id) {
        log.debug("REST request to delete Config : {}", id);
        configRepository.deleteById(id);
        hazelcastService.delete(id);
        reloadUIBackendProps();

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    private void reloadUIBackendProps() {
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Reload", "true");
    	HttpEntity<String> entity = new HttpEntity<>(headers);

    	restTemplate.put(hazelcastService.get("egov.eauth.sys.admin.reload.map.url"), entity);
    }
}
