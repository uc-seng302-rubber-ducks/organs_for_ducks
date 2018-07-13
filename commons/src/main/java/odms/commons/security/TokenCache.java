package odms.commons.security;

import odms.commons.model._abstract.TimedEntryCache;
import odms.commons.model.datamodel.TimedCacheValue;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Timed token cache to allow tokens that are inactive for to long to be removed.
 */
public class TokenCache implements TimedEntryCache<String, TimedCacheValue<String>> {


    private static final long TOKEN_TTL = 30; //time that a token should stay alive in minutes
    private ConcurrentMap<String, TimedCacheValue> cache;

    public TokenCache() {
        cache = new ConcurrentHashMap<>();
    }


    @Override
    public void add(String key, TimedCacheValue<String> value) {
        cache.put(key, value);
    }

    @Override
    public void removeOlderThan(LocalDateTime time) {
        for (Map.Entry<String, TimedCacheValue> entry : cache.entrySet()){
            if(entry.getValue().getDateTime().isBefore(time)){
                cache.remove(entry.getKey());
            }
        }
    }


    /**
     * returns the value corresponding with the given key.
     *
     * Updates expiry time by TOKEN_TTL to renew the key.
     *
     * @param key key to search by
     * @return V value if it exists, null otherwise
     */
    @Override
    public TimedCacheValue<String> get(String key) {
        if (cache != null && cache.containsKey(key)){
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime expiryTime = currentTime.plusMinutes(TOKEN_TTL);
            cache.get(key).setDateTime(expiryTime);
            return cache.get(key); // I have ignored this warning as there is no way to check this assignment
        }
        return null;
    }

    @Override
    public boolean evict(String key) {
        return cache.remove(key) != null;
    }


    /**
     * Method is intentionally left blank. If the application is restarted the token cache should not persist
     *
     * @param filename file name within the /CACHE folder to load from
     * @throws IOException Will never be thrown
     */
    @Override
    public void load(String filename) throws IOException {
        //Cache should never be saved, so nothing can be loaded
    }


    /**
     * Method is intentionally left blank. If the application is restarted the token cache should not persist
     *
     * @param filename file name within the /CACHE folder to load from
     * @throws IOException Will never be thrown
     */
    @Override
    public void save(String filename) throws IOException {
        //Cache should never be saved
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public boolean containsKey(String key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }
}
