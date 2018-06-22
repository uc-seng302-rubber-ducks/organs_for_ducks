package seng302.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model._enum.Organs;

import java.time.LocalDate;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

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

//    @Test
//    public void testMultipleChangeMultipleRedo() {
//        testUser.setBloodType("B+");
//        testUser.setCurrentAddress("42 wallaby way");
//        testUser.setRegion("Sydney");
//
//        testUser.undo();
//        testUser.undo();
//        testUser.undo();
//
//        testUser.redo();
//        testUser.redo();
//        testUser.redo();
//
//        assertEquals(testUser.getBloodType(), BloodTypes.BPLUS.toString());
//        assertEquals(testUser.getCurrentAddress(), "42 wallaby way");
//        assertEquals(testUser.getRegion(), "Sydney");
//    }

    @Test
    public void testMultipleChangesSingleRedo() {
        testUser.setBloodType("B+");
        testUser.setLastName("Jefferson");
        testUser.setLastName("Doe");

        testUser.undo();
        testUser.redo();

        assertEquals("Doe", testUser.getLastName());
    }

    @Test
    public void singleChangeMementoShouldContainTwoStates() {
        testUser.setName("Harold", "", "");
        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        assertTrue(mem.getOldObject() != null && mem.getNewObject() != null);
    }

    @Test
    public void singleChangeMementoShouldContainCorrectStates() {
        testUser.setName("Harold", "", "");
        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        String oldName = mem.getOldObject().getFullName();
        String newName = mem.getNewObject().getFullName();
        assertEquals("Frank", oldName);
        assertEquals("Harold", newName);

        //two states of the same user
        assertTrue(mem.getNewObject().equals(mem.getOldObject()));
    }

    @Test
    public void DonorAttributesAttachedUserIsCorrectWhenStored() {

        assertTrue(testUser.getDonorDetails().getAttachedUser().equals(testUser));
        testUser.setNhi("QWE1234");
        assertTrue(testUser.getDonorDetails().getAttachedUser().equals(testUser));

        testUser.undo();
        Memento<User> mem = testUser.getRedoStack().peek();
        User newUser = mem.getNewObject();
        User oldUser = mem.getOldObject();

        assertNotEquals(newUser, oldUser);
        assertTrue(oldUser.getDonorDetails().getAttachedUser().equals(oldUser));
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

    @Test
    @Ignore
    public void ReceiverAttributesAttachedUserIsCorrectWhenStored() {
        fail("TODO implement when receiver branch merged");
//    assert(testUser.getReceiverDetails().getAttachedUser().equals(testUser));
//    testUser.setNhi("QWE1234");
//    assert(testUser.getReceiverDetails().getAttachedUser().equals(testUser));
//
//    Memento<User> mem = testUser.getUndoStack().peek();
//    User newUser = mem.getNewObject();
//    User oldUser = mem.getOldObject();
//
//    assertNotEquals(newUser, oldUser);
//    assert(oldUser.getReceiverDetails().getAttachedUser().equals(oldUser));
//    assert(newUser.getReceiverDetails().getAttachedUser().equals(newUser));
    }
}
