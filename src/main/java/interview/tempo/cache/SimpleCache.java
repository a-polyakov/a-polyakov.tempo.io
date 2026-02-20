package interview.tempo.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();

    private final int maxSize;
    private final long ttlMs;

    public SimpleCache(int maxSize, long ttlMs, int evictionIntervalMs) {
        this.maxSize = maxSize;
        this.ttlMs = ttlMs;
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            releaseTtl();
                            try {
                                Thread.sleep(evictionIntervalMs);
                            } catch (InterruptedException ignore) {
                            }
                        }
                    }
                }
        );
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Puts a value into the cache.
     *
     * <p>If the cache size exceeds {@code maxSize}, the method first attempts to
     * remove expired entries (based on TTL). If no expired entries are found,
     * the oldest entry (by insertion timestamp) will be removed to make space
     * for the new value.
     *
     * @param key   cache key
     * @param value cache value
     */
    public void put(K key, V value) {
        long now = System.currentTimeMillis();

        if (cache.size() >= maxSize) {

            boolean removedExpired = false;
            if (!cache.isEmpty()) {
                for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
                    if (now - entry.getValue().timestamp() >= ttlMs) {
                        if (cache.remove(entry.getKey(), entry.getValue())) {
                            removedExpired = true;
                            break;
                        }
                    }
                }

                while (cache.size() >= maxSize || !removedExpired) {
                    Map.Entry<K, CacheEntry<V>> oldest = null;
                    long oldestTime = Long.MAX_VALUE;

                    for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
                        long ts = entry.getValue().timestamp();

                        if (ts < oldestTime) {
                            oldestTime = ts;
                            oldest = entry;
                        }
                    }

                    if (oldest != null) {
                        removedExpired=cache.remove(oldest.getKey(), oldest.getValue());
                    }
                }
            }
        }

        cache.put(key, new CacheEntry<>(value, now));
    }

    public V get(K key) {
        CacheEntry<V> cur = cache.computeIfPresent(key, (k, entry) -> {
            if (System.currentTimeMillis() - entry.timestamp() < ttlMs) {
                return entry;
            }
            return null;
        });
        return (cur != null) ? cur.value() : null;
    }

    public void releaseTtl() {
        for (Map.Entry<K, CacheEntry<V>> entry : cache.entrySet()) {
            if (System.currentTimeMillis() - entry.getValue().timestamp() >= ttlMs) {
                cache.remove(entry.getKey(), entry.getValue());
            }
        }
    }

    public int size() {
        return cache.size();
    }
}