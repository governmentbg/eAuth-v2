package bg.bulsi.egov.eauth.eid.provider.cash;

import java.time.OffsetDateTime;
import java.util.Enumeration;
import java.util.Optional;

public interface Cache<T> {
	
    public enum ExpiredType {
    	EXPIRED_PERIOD,
    	EXPIRED_TIME
    }

	void add(String key, T value, OffsetDateTime periodInMillis);

	void add(String key, T value, long periodInMillis, ExpiredType expiredType);
    
	@Deprecated
	void add(String key, T value);
 
    void remove(String key);
 
    T get(String key);

    Optional<String> getOldestKey();
 
    Enumeration<String> getKeys();

    void clear();
 
    long size();

	long sizeAll();

	long getExpiredTime(String key);

	boolean contains(String key);

    
    
}