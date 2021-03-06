package odms.commons.model;

import odms.commons.model._enum.Organs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

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

    @Test
    public void donorAttributesAttachedUserIsCorrectWhenStored() {

        assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));
        testUser.setNhi("QWE1234");
        assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));

        Memento<User> mem = testUser.getUndoStack().peek();
        User oldUser = mem.getState();

        assert (oldUser.getDonorDetails().getAttachedUser().equals(oldUser));
    }

    @Test
    public void testOrgansChanged() {
        testUser.getDonorDetails().addOrgan(Organs.BONE, null);
        testUser.getDonorDetails().addOrgan(Organs.BONE_MARROW, null);

        testUser.undo();

        Assert.assertTrue(!testUser.getDonorDetails().getOrgans().contains(Organs.BONE_MARROW));
    }
}
