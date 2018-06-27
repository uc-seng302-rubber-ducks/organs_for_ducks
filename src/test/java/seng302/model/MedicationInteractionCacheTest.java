package seng302.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model.datamodel.TimedCacheValue;

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
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldNotDuplcateABandBA() {
        //entries of medications a-b and b-a should not both exist
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldGetSingleEntry() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldGetNullForNonExistentEntry() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldEvictOldEntries() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldClearAllEntries() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldLoadSuccessfully() {
        Assert.fail("not yet implemented");
    }


    @Test
    public void shouldThrowXOnBadLoad() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldThrowYOnBadLoad() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void shouldRemoveOldData() {
        Assert.fail("not yet implemented");
    }


}
