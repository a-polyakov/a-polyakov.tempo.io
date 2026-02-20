package interview.tempo.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SimpleCache<K, V> {

    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<K> orderQueue = new ConcurrentLinkedQueue<>();

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
                        while (running) {
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
        cache.put(key, new CacheEntry<>(value, now));
        orderQueue.offer(key);

        while (cache.size() > maxSize) {
            K oldestKey = orderQueue.poll();
            if (oldestKey == null) break;
            CacheEntry<V> entry = cache.get(oldestKey);
            if (entry == null) continue;
            cache.remove(oldestKey, entry);
        }
    }

    private boolean isExpired(CacheEntry<V> e, long now) {
        return now - e.timestamp() >= ttlMs;
    }


    public V get(K key) {
        long now = System.currentTimeMillis();
        CacheEntry<V> cur = cache.computeIfPresent(key, (k, entry) -> {
            return isExpired(entry, now) ? null : entry;
        });
        return (cur != null) ? cur.value() : null;
    }

    public void releaseTtl() {
        long now = System.currentTimeMillis();
        while (true) {
            K key = orderQueue.peek();
            if (key == null) {
                return;
            }

            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                orderQueue.poll();
                continue;
            }

            if (!isExpired(entry, now)) {
                return;
            }

            orderQueue.poll();
            cache.remove(key, entry);
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