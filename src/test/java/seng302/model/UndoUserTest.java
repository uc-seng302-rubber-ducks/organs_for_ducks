package seng302.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model._enum.Organs;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * These tests are done on Users but are more focused on testing the undo stacks/memento system
 */
public class UndoUserTest {

    private User testUser;

    @Before
    public void setUp() {
        testUser = new User("Frank", LocalDate.of(1980, 3, 5), "ABC1234");
    }

    @Test
    public void testSingleChangeSingleUndo() {
        testUser.setName("Geoff", "", "");
        testUser.undo();
        assertEquals("Frank", testUser.getFullName());
    }

    @Test
    public void testSingleChangeMultipleUndo() {
        testUser.setName("Geoff", "", "");
        testUser.undo();
        testUser.undo();
        testUser.undo();
        assertEquals("Frank", testUser.getFullName());
    }

    @Test
    @Ignore
    public void testMultipleChangeMultipleUndo() {
        testUser.setBloodType("B+");
        testUser.setStreetName("wallaby way");
        testUser.setStreetNumber("42");
        testUser.setRegion("Sydney");

        testUser.undo();
        assertNull(testUser.getRegion());

        testUser.undo();
        assertNull(testUser.getAddress());

        testUser.undo();
        assertEquals("U", testUser.getBloodType());
    }

    @Test
    public void testMultipleChangesSingleUndo() {
        testUser.setBloodType("B+");
        testUser.setLastName("Jefferson");
        testUser.setLastName("Doe");

        testUser.undo();

        assertEquals("Jefferson", testUser.getLastName());
    }

    @Test //CHANGED
    public void singleChangeMementoShouldContainTwoStates() {
        testUser.setName("Harold", "", "");
        Memento<User> mem = testUser.getUndoStack().peek();
        assert (mem.getState() != null);
    }

    @Test //CHANGED
    public void singleChangeMementoShouldContainCorrectStates() {
        testUser.setName("Harold", "", "");
        Memento<User> mem = testUser.getUndoStack().peek();
        String oldName = mem.getState().getFullName();
        assertEquals("Frank", oldName);

        //two states of the same user //(James) dunno what this line was supposed to be
        //assert (mem.getNewObject().equals(mem.getOldObject()));
    }

    @Ignore //I think this test is out of date now
    @Test
    public void multipleChangesConsecutiveMementosShouldShareState() {
        //state after one change should be the state before the next change
        testUser.setName("Geoff", "", "");
        Memento<User> firstMem = testUser.getUndoStack().peek();

        testUser.setName("Harold", "", "");
        Memento<User> secondMem = testUser.getUndoStack().peek();

        assert (firstMem.getNewObject().equals(secondMem.getOldObject()));
    }

    @Test //CHANGED
    public void DonorAttributesAttachedUserIsCorrectWhenStored() {

        assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));
        testUser.setNhi("QWE1234");
        assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));

        Memento<User> mem = testUser.getUndoStack().peek();
        User oldUser = mem.getState();

        assert (oldUser.getDonorDetails().getAttachedUser().equals(oldUser));
    }

    @Test
    public void testOrgansChanged() {
        testUser.getDonorDetails().addOrgan(Organs.BONE);
        testUser.getDonorDetails().addOrgan(Organs.BONE_MARROW);

        testUser.undo();

        Assert.assertTrue(!testUser.getDonorDetails().getOrgans().contains(Organs.BONE_MARROW));
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
