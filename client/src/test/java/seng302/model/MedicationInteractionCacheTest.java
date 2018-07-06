package seng302.model;

import odms.commons.model.MedicationInteractionCache;
import odms.commons.model.datamodel.TimedCacheValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class MedicationInteractionCacheTest {

    private MedicationInteractionCache cache;

    @Before
    public void setUp() {
        cache = new MedicationInteractionCache();
    }

    @Test
    public void shouldAddSingleEntry() {
        String key = "newKey";
        String value = "value";

        cache.add(key, new TimedCacheValue<>(value));

        Assert.assertTrue(cache.containsKey(key));
    }

    @Test
    public void shouldAddEntryByDrugNames() {
        String drugA = "first";
        String drugB = "second";
        String result = "death, or worse";
        cache.add(drugA, drugB, result);

        Assert.assertTrue(cache.containsKey("first-second"));
    }

    @Test
    public void shouldNotDuplicateABandBA() {
        //entries of medications a-b and b-a should not both exist
        String drugA = "first";
        String drugB = "second";
        String result = "death, or worse";

        cache.add(drugB, drugA, result);
        cache.add(drugA, drugB, result);

        Assert.assertTrue(cache.containsKey("first-second"));
        Assert.assertFalse(cache.containsKey("second-first"));
    }

    @Test
    public void shouldGetSingleEntry() {
        TimedCacheValue<String> expected = new TimedCacheValue<>("value");
        cache.add("key", expected);
        TimedCacheValue actual = cache.get("key");

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNullForNonExistentEntry() {
        Assert.assertNull(cache.get("something"));
    }

    @Test
    public void shouldEvictOldEntries() {
        TimedCacheValue<String> oldValue = new TimedCacheValue<>("value");
        oldValue.setDateTime(LocalDateTime.of(2018, 6, 11, 0, 0));
        //leave time as default "now" for new value
        TimedCacheValue<String> newValue = new TimedCacheValue<>("value2");

        cache.add("oldKey", oldValue);
        cache.add("newKey", newValue);

        cache.removeOlderThan(LocalDateTime.of(2018, 6, 12, 0, 0));
        Assert.assertFalse(cache.containsKey("oldKey"));
    }

    @Test
    public void shouldClearAllEntries() {
        cache.add("key1", new TimedCacheValue<>("val1"));
        cache.add("key2", new TimedCacheValue<>("val2"));
        cache.add("key3", new TimedCacheValue<>("val3"));
        cache.clear();
        Assert.assertTrue(cache.isEmpty());
    }
}
