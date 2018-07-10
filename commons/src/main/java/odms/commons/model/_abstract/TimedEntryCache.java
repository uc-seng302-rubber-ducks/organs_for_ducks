package odms.commons.model._abstract;

import odms.commons.model.datamodel.TimedCacheValue;

import java.time.LocalDateTime;

/**
 * a cache that records the time a value was stored
 *
 * @param <K> type of the key
 * @param <V> type of the value to store.
 * @see Cache
 * @see TimedCacheValue
 */
public interface TimedEntryCache<K, V extends TimedCacheValue> extends Cache<K, V> {

    /**
     * removes all stored values older than the given time
     *
     * @param time maximum age of data to keep
     */
    void removeOlderThan(LocalDateTime time);
}
