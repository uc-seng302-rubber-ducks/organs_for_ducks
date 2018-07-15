package odms.model;

import odms.commons.model.MedicationInteractionCache;
import odms.commons.model._enum.Directory;
import odms.commons.model.datamodel.TimedCacheValue;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MedicationInteractionCacheFileTest {
    private final String filename = "testInteractionsCache";
    private MedicationInteractionCache cache;

    @Before
    public void setUp() {
        cache = new MedicationInteractionCache();
    }

    @After
    public void tearDown() {
        //remove any files that were created within the tests
        File outFile = new File(Directory.CACHE.directory() + filename);
        if (outFile.exists()) {
            outFile.delete();
        }
    }

    @Test
    public void shouldLoadSuccessfully() throws IOException {
        copyFile("valid.json");

        cache.load(filename);
        Assert.assertTrue(cache.containsKey("key1"));
        Assert.assertTrue(cache.containsKey("key2"));
        Assert.assertTrue(cache.containsKey("key3"));
    }

    @Test
    public void shouldLoadEmptyJsonObject() throws IOException {
        cache.add("key0", new TimedCacheValue<>("value0"));
        copyFile("empty.json");

        cache.load(filename);
        Assert.assertTrue(cache.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionOnLoadEmptyFile() throws IOException {
        cache.add("key0", new TimedCacheValue<>("value0"));
        copyFile("null.json");

        cache.load(filename);
        Assert.assertTrue(cache.containsKey("key0"));

    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionNotLoadWrongFileType() throws IOException {
        copyFile("oops.md");
        cache.load(filename);
    }


    @Test(expected = FileNotFoundException.class)
    public void shouldThrowFileNotFoundWhenNoFileGiven() throws IOException {
        cache.load(filename);
        Assert.fail("not yet implemented");
    }

    /**
     * copies one of the test files to dir and renames it to {filename}
     *
     * @param name name of test file to copy
     */
    private void copyFile(String name) {
        try {
            Files.createDirectories(Paths.get(Directory.CACHE.directory()));

            File file = new File("src/test/resources/medicationInteractionCacheFiles/" + name);
            Files.copy(file.toPath(),
                    new File(Directory.CACHE.directory() + filename).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Assert.fail("Error in setting up files: " + ex.getMessage());
        }
    }
}
