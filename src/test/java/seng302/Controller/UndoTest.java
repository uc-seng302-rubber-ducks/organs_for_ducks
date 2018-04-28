package seng302.Controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Model.Memento;
import seng302.Model.User;

/**
 * These tests are done on Users but are more focused on testing the undo stacks/memento system
 */
public class UndoTest {

  private User testUser;

  @Before
  public void setUp() {
    testUser = new User("Frank", LocalDate.of(1980, 3, 5), "ABC1234");
  }

  @Test
  public void testSingleChangeSingleUndo() {
    testUser.setName("Geoff");
    testUser.undo();
    assertEquals("Frank", testUser.getName());
  }

  @Test
  public void testSingleChangeMultipleUndo() {
    testUser.setName("Geoff");
    testUser.undo();
    testUser.undo();
    testUser.undo();
    assertEquals("Frank", testUser.getName());
  }

  @Test
  public void testMultipleChangeMultipleUndo() {
    testUser.setBloodType("B+");
    testUser.setCurrentAddress("42 wallaby way");
    testUser.setRegion("Sydney");

    testUser.undo();
    assertNull(testUser.getRegion());

    testUser.undo();
    assertNull(testUser.getCurrentAddress());

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

  @Test
  public void singleChangeMementoShouldContainTwoStates() {
    testUser.setName("Harold");
    Memento<User> mem = testUser.getUndoStack().peek();
    assert (mem.getOldObject() != null && mem.getNewObject() != null);
  }

  @Test
  public void singleChangeMementoShouldContainCorrectStates() {
    testUser.setName("Harold");
    Memento<User> mem = testUser.getUndoStack().peek();
    String oldName = mem.getOldObject().getName();
    String newName = mem.getNewObject().getName();
    assertEquals("Frank", oldName);
    assertEquals("Harold", newName);

    //two states of the same user
    assert (mem.getNewObject().equals(mem.getOldObject()));
  }

  @Test
  public void multipleChangesConsecutiveMementosShouldShareState() {
    //state after one change should be the state before the next change
    testUser.setName("Geoff");
    Memento<User> firstMem = testUser.getUndoStack().peek();

    testUser.setName("Harold");
    Memento<User> secondMem = testUser.getUndoStack().peek();

    assert (firstMem.getNewObject().equals(secondMem.getOldObject()));
  }

  @Test
  public void DonorAttributesAttachedUserIsCorrectWhenStored() {

    assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));
    testUser.setNhi("QWE1234");
    assert (testUser.getDonorDetails().getAttachedUser().equals(testUser));

    Memento<User> mem = testUser.getUndoStack().peek();
    User newUser = mem.getNewObject();
    User oldUser = mem.getOldObject();

    assertNotEquals(newUser, oldUser);
    assert (oldUser.getDonorDetails().getAttachedUser().equals(oldUser));
    assert (newUser.getDonorDetails().getAttachedUser().equals(newUser));
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
