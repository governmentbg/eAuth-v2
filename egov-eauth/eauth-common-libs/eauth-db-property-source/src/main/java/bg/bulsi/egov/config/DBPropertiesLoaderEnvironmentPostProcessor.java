package bg.bulsi.egov.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This is used to load property values from the database into the spring application environment
 * so that @Value annotated fields in the various beans can be populated with these database based
 * values.   I can also be used to store spring boot configuration parameters
 * <p>
 * In order for Spring to use this post porcessor this class needs to be added into the META-INF/spring.factories file like so:
 * org.springframework.boot.env.EnvironmentPostProcessor=my.package.name.DBPropertiesLoaderEnvironmentPostProcessor
 * <p>
 * It will look for the spring boot dataseource properties that traditionally get stored in the application.yml files and use
 * those to create a connection to the database to load the properties.  It first looks for the datasource jndi name property
 * and if that fails it looks for the Spring.datasource.url based properties.
 */
@Slf4j
public class DBPropertiesLoaderEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication application) {
        System.out.println("***********************************Pulling properties from the database***********************************");
        if (env.getProperty("spring.datasource.jndi-name") != null) {
            log.info("Extracting properties from the database using spring.datasource.jndi-name");
            try {
                JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
                dsLookup.setResourceRef(true);
                DataSource ds = dsLookup.getDataSource(env.getProperty("spring.datasource.jndi-name"));
                String schema = env.getProperty("database-property-source.schema", "public");
                try (Connection con = ds.getConnection()) {
                    env.getPropertySources().addFirst(new DataBasePropertySource(con, schema));
                }
                log.info("Configuration properties were loaded from the database via JNDI Lookup");
            } catch (DataSourceLookupFailureException | SQLException e) {
                log.error("Error creating properties from database with jndi lookup", e);
                e.printStackTrace();
            }
        } else if (env.getProperty("spring.datasource.url") != null) {
            String url = env.getProperty("spring.datasource.url");
            String driverClass = env.getProperty("spring.datasource.driver-class-name");
            String username = env.getProperty("spring.datasource.username");
            String password = env.getProperty("spring.datasource.password");
            String schema = env.getProperty("database-property-source.schema", "public");
            try {
                DriverManager.registerDriver((Driver) Class.forName(driverClass).newInstance());
                try (Connection c = DriverManager.getConnection(url, username, password)) {
                    env.getPropertySources().addFirst(new DataBasePropertySource(c, schema));
                    log.info("Configuration properties were loaded from the database via manual connection creation");
                }
            } catch (Exception e) {
                log.error("Error creating properties from database with manual connection creation.", e);
            }
        } else {
            log.error("Could not load properties from the database because no spring.datasource properties were present");
        }

    }

    /**
     * An implementation of springs PropertySource class that sources from a
     * {@link DataBasedBasedProperties} instance which is java.util.Properties class that
     * pulls its data from the database..
     */
    static class DataBasePropertySource extends EnumerablePropertySource<DataBasedBasedProperties> {

        DataBasePropertySource(Connection c, String schema) {
            super("DataBasePropertySource", new DataBasedBasedProperties(c, schema));
        }

        /* (non-Javadoc)
         * @see org.springframework.core.env.PropertySource#getProperty(java.lang.String)
         */
        @Override
        public Object getProperty(String name) {
            return getSource().get(name);
        }

        @Override
        public String[] getPropertyNames() {
            return getSource().getPropertyNames();
        }
    }

    /**
     * Pulls name and value strings from a database table named properties
     */
    static class DataBasedBasedProperties extends Properties {
        private static final long serialVersionUID = 1L;

        private String[] propertyNames;

        DataBasedBasedProperties(Connection con, String schema) {
            List<String> names = new ArrayList<>();
            try (
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(String.format("SELECT property_key, property_value FROM %s.db_property_source", schema))
            ) {
                while (rs.next()) {
                    String name = rs.getString(1);
                    String value = rs.getString(2);
                    names.add(name);
                    setProperty(name, value);
                }
                propertyNames = names.toArray(new String[names.size()]);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        String[] getPropertyNames() {
            return propertyNames;
        }

    }
}