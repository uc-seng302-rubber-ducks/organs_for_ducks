package seng302.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model._enum.BloodTypes;
import seng302.model._enum.Organs;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * These tests are done on Users but are more focused on testing the undo stacks/memento system
 */
public class RedoTest {

    private User testUser;

    @Before
    public void setUp() {
        testUser = new User("Frank", LocalDate.of(1980, 3, 5), "ABC1234");
    }

    @Test
    public void testSingleChangeSingleRedo() {
        testUser.setName("Geoff", "", "");
        testUser.undo();
        testUser.redo();
        assertEquals("Geoff", testUser.getFullName());
    }

    @Test
    public void testSingleChangeMultipleRedo() {
        testUser.setName("Geoff", "", "");
        testUser.undo();
        testUser.redo();
        testUser.redo();
        testUser.redo();
        assertEquals("Geoff", testUser.getFullName());
    }

    @Test
    @Ignore
    public void testMultipleChangeMultipleRedo() {
        testUser.setBloodType("B+");
        testUser.setStreetName("wallaby way");
        testUser.setStreetNumber("42");
        testUser.setRegion("Sydney");

        testUser.undo();
        testUser.undo();
        testUser.undo();

        testUser.redo();
        testUser.redo();
        testUser.redo();

        assertEquals(testUser.getBloodType(), BloodTypes.BPLUS.toString());
        assertEquals(testUser.getAddress(), "42 wallaby way \n" +
                "\n" +
                " Sydney");
        assertEquals(testUser.getRegion(), "Sydney");
    }

    @Test
    public void testMultipleChangesSingleRedo() {
        testUser.setBloodType("B+");
        testUser.setLastName("Jefferson");
        testUser.setLastName("Doe");

        testUser.undo();
        testUser.redo();

        assertEquals("Doe", testUser.getLastName());
    }

    @Test //CHANGED
    public void singleChangeMementoShouldContainTwoStates() {
        testUser.setName("Harold", "", "");
        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        assertTrue(mem.getState() != null);
    }

    @Test //CHANGED
    public void singleChangeMementoShouldContainCorrectStates() {
        testUser.setName("Harold", "", "");
        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        String newName = mem.getState().getFullName();
        assertEquals("Harold", newName);

        //two states of the same user
        //assertTrue(mem.getNewObject().equals(mem.getOldObject()));
    }

    @Test //CHANGED
    public void DonorAttributesAttachedUserIsCorrectWhenStored() {

        assertTrue(testUser.getDonorDetails().getAttachedUser().equals(testUser));
        testUser.setNhi("QWE1234");
        assertTrue(testUser.getDonorDetails().getAttachedUser().equals(testUser));

        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        User newUser = mem.getState();

        User test = newUser.getDonorDetails().getAttachedUser();
        assertTrue(newUser.getDonorDetails().getAttachedUser().equals(newUser));
    }

    @Test
    public void testOrgansRedo() {
        testUser.getDonorDetails().addOrgan(Organs.BONE);
        testUser.getDonorDetails().addOrgan(Organs.BONE_MARROW);

        testUser.undo();
        testUser.redo();

        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(Organs.BONE));
        Assert.assertTrue(testUser.getDonorDetails().getOrgans().contains(Organs.BONE_MARROW));
    }
}
