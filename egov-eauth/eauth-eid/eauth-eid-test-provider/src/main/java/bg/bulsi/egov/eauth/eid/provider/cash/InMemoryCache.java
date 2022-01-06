package bg.bulsi.egov.eauth.eid.provider.cash;

import java.lang.ref.SoftReference;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import java.lang.reflect.ParameterizedType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryCache<T> implements Cache<T> {

	private static final int CLEAN_UP_PERIOD_IN_SEC = 300; // 86400s

	private final ConcurrentHashMap<String, SoftReference<CacheObject<T>>> cache = new ConcurrentHashMap<>();

	public InMemoryCache() {
		Thread cleanerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * 1000);
					cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
							.map(CacheObject::isExpired).orElse(false));
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		cleanerThread.setDaemon(true);
		cleanerThread.start();
	}

	/**
	 * Set expiryTime = CLEAN_UP_PERIOD_IN_SEC * 1000 ms [86400s = 24h]
	 */
	@SuppressWarnings("unused")
	@Override
	@Deprecated
	public void add(String key, T value) {
		if (key == null) {
			return;
		}
		if (false && value == null) {
			cache.remove(key);
		} else {
			long expiryTime = System.currentTimeMillis() + CLEAN_UP_PERIOD_IN_SEC * 1000;
			cache.put(key, new SoftReference<>(new CacheObject<T>(value, expiryTime)));
		}
	}

	@SuppressWarnings("unused")
	@Override
	@Deprecated
	public void add(String key, T value, OffsetDateTime offsetDateTime) {
		if (key == null) {
			return;
		}
		if (false && value == null) {
			cache.remove(key);
		} else {
			long expiryTime = offsetDateTime.toInstant().toEpochMilli();
			log.debug("###ADD new entry with key '{}' in '{}' Exp. OffsetDateTime {}, TimeLife {} ms", key,
					value.getClass().getSimpleName(), expiryTime, expiryTime-Calendar.getInstance().getTime().getTime());
			cache.put(key, new SoftReference<>(new CacheObject<T>(value, expiryTime)));
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void add(String key, T value, long periodInMillis, ExpiredType expiredType) {
		if (key == null) {
			return;
		}
		if (false && value == null) {
			cache.remove(key);
		} else {

			long expiryTime = 0;
			switch (expiredType) {
			case EXPIRED_PERIOD:
				expiryTime = System.currentTimeMillis() + periodInMillis;
				break;
			case EXPIRED_TIME:
				expiryTime = periodInMillis;
				break;
			default: // if NULL
				expiryTime = System.currentTimeMillis() + CLEAN_UP_PERIOD_IN_SEC * 1000;
				break;
			}
			log.debug("###ADD new entry with key '{}' in '{}' Exp.time ms: {}, TimeLife {}ms", key,
					value.getClass().getSimpleName(), expiryTime, expiryTime-Calendar.getInstance().getTime().getTime());
			cache.put(key, new SoftReference<>(new CacheObject<T>(value, expiryTime)));
		}
	}

	@Override
	public void remove(String key) {
		cache.remove(key);
		//String tclass = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getTypeName();
		log.debug("Remove {} from '{}' Cache!", key, Arrays.toString(this.getClass().getDeclaredClasses()));
	}
	
	@Override
	public boolean contains(String key) {
		return cache.containsKey(key);
	}

	@Override
	public T get(String key) {
		return Optional.ofNullable(cache.get(key)).map(SoftReference::get)
				.filter(cacheObject -> !cacheObject.isExpired()).map(CacheObject::getValue).orElse(null);
	}

	@Override
	public Optional<String> getOldestKey() {
		// Set<Entry<String, SoftReference<CacheObject<T>>>> set = cache.sentrySet();

		String key = null;
		log.debug("Search for oldest entry of {} ",cache.size());
		if (cache.size() == 1) {
			key = (String) cache.keySet().toArray()[0];
		} else if (cache.size() > 1) {
			// XXX Something is wrong
			Entry<String, SoftReference<CacheObject<T>>> row = cache.entrySet().stream()
					.min(Comparator.comparingLong(entry -> entry.getValue().get().expiryTime))
					.filter(entry -> !entry.getValue().get().isExpired()).orElse(null);

			if (row != null) {
				log.debug("Found oldest key: {} from {}", row.getKey(), row.getValue().get().expiryTime);
				key = row.getKey();
			}
		}
		return Optional.ofNullable(key);
	}

	@Override
	public Enumeration<String> getKeys() {
		return cache.keys();
	}

	@Override
	public void clear() {
		cache.clear();
	}

	@Override
	public long size() {
		return cache.entrySet().stream().filter(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get)
				.map(cacheObject -> !cacheObject.isExpired()).orElse(false)).count();
	}

	@Override
	public long sizeAll() {
		return cache.size();
	}

	@AllArgsConstructor
	private static class CacheObject<T> {

		@Getter
		private T value;
		@Getter
		private long expiryTime;

		// TODO restore system to UTC, if fixed expirity to UTC
		boolean isExpired() {
			LocalDateTime date = java.time.LocalDateTime.now();
			ZoneId zone = ZoneId.systemDefault();
			ZoneOffset zoneOffSet = zone.getRules().getOffset(date);
			long currentTimeMS = date.atOffset(zoneOffSet).toInstant().toEpochMilli();
			boolean expired = false;
			// log.debug("Curr.Date: {}", date);
			// log.debug("Offset: {}", offset);
			expired = currentTimeMS > expiryTime;
			log.debug("Check cache '{}' for EXPIRED {}, ms to live {};\tSum.exp: {}\tExp.time {}",
					value.getClass().getSimpleName(), expired, expiryTime - currentTimeMS, currentTimeMS, expiryTime);
			return expired;
		}
	}

	@Override
	public long getExpiredTime(String key) {
		return Optional.ofNullable(cache.get(key)).map(SoftReference::get).map(CacheObject::getExpiryTime).orElse(null);
	}

	@Override
	public String toString() {
		for (Entry<String, SoftReference<CacheObject<T>>> entry : cache.entrySet()) {
			log.info("ID: {} -> exp: {} || now: {}", entry.getKey(), entry.getValue().get().expiryTime,
					System.currentTimeMillis());
		}
		return super.toString();
	}

	public static void main(String[] args) {

		CharSequence text = new StringBuffer("2020-07-06T15:24:59.545+03:00");
		OffsetDateTime dateT = OffsetDateTime.parse(text);

		System.out.println("OffsetData: " + dateT);
		System.out.println("OffsetData LONG: " + dateT.toEpochSecond() * 1000);
		System.out.println("OffsetData LONG through instant: " + dateT.toInstant().toEpochMilli());
		System.out.println("OffsetData Offset: " + dateT.getOffset());
		System.out.println("OffsetData Offset: " + dateT.getOffset().getTotalSeconds() * 1000);

		long date = System.currentTimeMillis();
		int offsetS = TimeZone.getDefault().getOffset(date);

		System.out.println("System date: " + new Date(date));
		System.out.println("System date: " + date);
		System.out.println("System offset: " + offsetS);
		System.out.println("System date w/ offset: " + (date + offsetS));
	}

}
