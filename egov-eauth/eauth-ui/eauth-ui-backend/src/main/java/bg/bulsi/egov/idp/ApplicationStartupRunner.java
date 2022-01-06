package bg.bulsi.egov.idp;

import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationStartupRunner implements CommandLineRunner {

	@Autowired
	private HazelcastService hcService;
	
	@Autowired
	private HazelcastInstance hazelcastInstance;
	
	
	@Override
	public void run(String... args) throws Exception {
		hcService.initializePropertiesMap();
	    IMap<Object, Object> props = hazelcastInstance.getMap(HazelcastConfiguration.PROPS_MAP);
	    log.info("props map size: [{}]", props.size());
	}

}
