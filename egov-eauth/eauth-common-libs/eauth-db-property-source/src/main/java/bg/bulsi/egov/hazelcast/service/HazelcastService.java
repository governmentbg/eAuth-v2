package bg.bulsi.egov.hazelcast.service;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import bg.bulsi.egov.hazelcast.config.HazelcastConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HazelcastService {

	@Qualifier("hazelcastInstance")
	@Autowired
	private HazelcastInstance hzInstance;

	@Autowired
	private ConfigurableEnvironment env;
	
	public void initializePropertiesMap() {
		initializePropertiesMap(false);
	}
	
	public void initializePropertiesMap(boolean clear) {
		IMap<String, String> properties = hzInstance.getMap(HazelcastConfiguration.PROPS_MAP);
		if (clear) {
			properties.clear();
		}
		loadPropertiesFromDB(properties);
	}
	
	public void loadPropertiesFromDB(IMap<String, String> properties) {
		
		if (env.getProperty("spring.datasource.url") != null) {
			String url = env.getProperty("spring.datasource.url");
			String driverClass = env.getProperty("spring.datasource.driver-class-name");
			String username = env.getProperty("spring.datasource.username");
			String password = env.getProperty("spring.datasource.password");
			String schema = env.getProperty("database-property-source.schema", "public");
			try {
				DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
				try (
					Connection conn = DriverManager.getConnection(url, username, password);
					Statement stmt = conn.createStatement();
		            ResultSet rs = stmt.executeQuery(String.format("SELECT property_key, property_value FROM %s.db_property_source", schema))	
				) {
					while (rs.next()) {
						// key -> value
						properties.put(rs.getString(1), rs.getString(2));
					}
					log.info("Configuration properties were loaded from the database into Hazelcast map via manual connection creation");
				}
			} catch (Exception e) {
				log.error("Error loading properties from database into Hazelcast map with manual connection creation.", e);
			}
		} else {
            log.error("Could not load properties from the database into Hazelcast map because no spring.datasource properties were present");
        }
		
	}
	
	public IMap<String, String> all() {
		return hzInstance.getMap(HazelcastConfiguration.PROPS_MAP);
	}

	public String get(String key) {
		return (String) hzInstance.getMap(HazelcastConfiguration.PROPS_MAP).get(key);
	}

	public void put(String key, String value) {
		hzInstance.getMap(HazelcastConfiguration.PROPS_MAP).put(key, value);
	}

	public void delete(String key) {
		hzInstance.getMap(HazelcastConfiguration.PROPS_MAP).delete(key);
	}
	
	public void clear() {
		hzInstance.getMap(HazelcastConfiguration.PROPS_MAP).clear();
	}
}
