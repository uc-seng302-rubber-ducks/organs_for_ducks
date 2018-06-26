package seng302.model._abstract;

import seng302.model.datamodel.TimedCacheValue;

import java.time.LocalDateTime;

/**
 * a cache that records the time a value was stored
 *
 * @param <K> type of the key
 * @param <V> type of the value to store.
 * @see seng302.model._abstract.Cache
 * @see seng302.model.datamodel.TimedCacheValue
 */
public interface TimedEntryCache<K, V extends TimedCacheValue> extends Cache<K, V> {

    /**
     * removes all stored values older than the given time
     *
     * @param time maximum age of data to keep
     */
    void removeOlderThan(LocalDateTime time);
}
