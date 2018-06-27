package seng302.model._abstract;

import java.io.IOException;

/**
 * generic cache interface intended for holding api data
 *
 * @param <K> type of the key to be stored
 * @param <V> type of the values to be stored
 */
public interface Cache<K, V> {

    /**
     * puts new value into the cache with k/v pair
     * will replace existing data if the key already exists
     *
     * @param key   key of the data being added. this will be used to get it back out later
     * @param value value being stored
     */
    void add(K key, V value);

    /**
     * returns the value corresponding with the given key.
     *
     * @param key key to search by
     * @return V value if it exists, null otherwise
     */
    V get(K key);

    /**
     * remove a single entry from the cache
     *
     * @param key key to remove
     * @return true if a k/v pair was removed
     */
    boolean evict(K key);

    /**
     * imports cache from a file
     *
     * @param filename file name within the /CACHE folder to load from
     * @throws IOException if there was an error reading the file
     */
    void load(String filename) throws IOException;

    /**
     * saves cache to a file
     *
     * @param filename file name within the /CACHE/ folder to save to
     * @throws IOException if there was an error writing to the file
     */
    void save(String filename) throws IOException;

    /**
     * removes all entries from the cache
     */
    void clear();

    /**
     * checks if the cache contains a given key
     *
     * @param key object to search for
     * @return true if key is in the cache, false otherwise
     */
    boolean containsKey(K key);

    /**
     * @return true if the cache contains no entries
     */
    boolean isEmpty();


}
