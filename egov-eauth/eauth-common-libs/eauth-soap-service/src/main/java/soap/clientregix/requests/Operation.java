package soap.clientregix.requests;

/**
 * Defines a common interface for all enums that specify operations to be executed
 * 
 * @author bozhanov
 *
 */
public interface Operation {

    /**
     * A string key used by RegiX to identify operations
     * @return
     */
    String getKey();
}
