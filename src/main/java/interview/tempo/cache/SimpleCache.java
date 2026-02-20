package interview.tempo.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleCache<K, V> {

    private final ConcurrentHashMap<K, CacheValue<V>> cache = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<CacheKey<K>> orderQueue = new ConcurrentLinkedQueue<>();

    private final int maxSize;
    private final long ttlMs;

    private volatile boolean running = true;
    private final Thread thread;

    public SimpleCache(int maxSize, long ttlMs, int evictionIntervalMs) {
        this.maxSize = maxSize;
        this.ttlMs = ttlMs;
        thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (running && !Thread.currentThread().isInterrupted()) {
                            releaseTtl();
                            try {
                                Thread.sleep(evictionIntervalMs);
                            } catch (InterruptedException ignore) {
                                Thread.currentThread().interrupt();
                                break;
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
        cache.put(key, new CacheValue<>(value, now));
        orderQueue.offer(new CacheKey<>(key, now));

        while (cache.mappingCount() > maxSize) {
            CacheKey<K> oldest = orderQueue.poll();
            if (oldest == null) break;
            CacheValue<V> entry = cache.get(oldest.key());
            if (entry == null) continue;
            if (entry.timestamp() == oldest.timestamp()) {
                cache.remove(oldest.key(), entry);
            }
        }
    }

    private boolean isExpired(CacheValue<V> e, long now) {
        return now - e.timestamp() >= ttlMs;
    }


    public V get(K key) {
        long now = System.currentTimeMillis();
        CacheValue<V> cur = cache.computeIfPresent(key, (k, entry) -> {
            return isExpired(entry, now) ? null : entry;
        });
        return (cur != null) ? cur.value() : null;
    }

    public void releaseTtl() {
        long now = System.currentTimeMillis();
        while (true) {
            CacheKey<K> oldest = orderQueue.peek();
            if (oldest == null) {
                return;
            }

            CacheValue<V> entry = cache.get(oldest.key());
            if (entry == null) {
                orderQueue.poll();
                continue;
            }

            if (entry.timestamp() != oldest.timestamp()) {
                orderQueue.poll();
                continue;
            }

            if (!isExpired(entry, now)) {
                return;
            }

            orderQueue.poll();
            cache.remove(oldest.key(), entry);
        }
    }

    public int size() {
        return cache.size();
    }

    public void shutdown() {
        running = false;
        thread.interrupt();
    }
}