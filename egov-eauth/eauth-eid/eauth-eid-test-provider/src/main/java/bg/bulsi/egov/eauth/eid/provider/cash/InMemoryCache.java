package bg.bulsi.egov.eauth.eid.provider.cash;

import java.lang.ref.SoftReference;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InMemoryCache<T> implements Cache<T> {

	private static final int CLEAN_UP_PERIOD_IN_SEC = 86400;

	private final ConcurrentHashMap<String, SoftReference<CacheObject<T>>> cache = new ConcurrentHashMap<>();


	public InMemoryCache() {
		Thread cleanerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					Thread.sleep(CLEAN_UP_PERIOD_IN_SEC * 1000);
					cache.entrySet().removeIf(entry -> Optional.ofNullable(entry.getValue()).map(SoftReference::get).map(CacheObject::isExpired).orElse(false));
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
	public void add(String key, T value, OffsetDateTime offsetDateTime) {
		if (key == null) {
			return;
		}
		if (false && value == null) {
			cache.remove(key);
		} else {
			long expiryTime = offsetDateTime.toInstant().toEpochMilli();
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
				default: //if NULL
					expiryTime = System.currentTimeMillis() + CLEAN_UP_PERIOD_IN_SEC * 1000;
					break;
			}

			cache.put(key, new SoftReference<>(new CacheObject<T>(value, expiryTime)));
		}
	}


	@Override
	public void remove(String key) {
		cache.remove(key);
	}


	@Override
	public T get(String key) {
		return Optional.ofNullable(cache.get(key))
				.map(SoftReference::get)
				.filter(cacheObject -> !cacheObject.isExpired())
				.map(CacheObject::getValue).orElse(null);
	}


	@Override
	public Optional<String> getOldestKey() {
		Set<Entry<String, SoftReference<CacheObject<T>>>> set = cache.entrySet();

		String key = null;

		if (set.size() == 1) {
			key = set.iterator().next().getKey();
		} else if (set.size() > 1) {

			Entry<String, SoftReference<CacheObject<T>>> row = cache.entrySet()
					.stream()
					.min(Comparator.comparingLong(entry -> entry.getValue().get().expiryTime))
					.filter(entry -> !entry.getValue().get().isExpired())
					.orElse(null);

			if (row != null) {
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
		return cache.entrySet().stream()
				.filter(entry -> Optional
						.ofNullable(entry.getValue())
						.map(SoftReference::get)
						.map(cacheObject -> !cacheObject.isExpired())
						.orElse(false))
				.count();
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


		boolean isExpired() {
			return System.currentTimeMillis() > expiryTime;
		}
	}


	@Override
	public long getExpiredTime(String key) {
		return Optional.ofNullable(cache.get(key))
				.map(SoftReference::get)
				.map(CacheObject::getExpiryTime).orElse(null);
	}


	@Override
	public String toString() {
		for (Entry<String, SoftReference<CacheObject<T>>> entry: cache.entrySet()) {
			log.info("ID: {} -> exp: {} || now: {}", entry.getKey(), entry.getValue().get().expiryTime, System.currentTimeMillis());
		}
		return super.toString();
	}
}
