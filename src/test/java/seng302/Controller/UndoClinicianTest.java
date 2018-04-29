package seng302.Controller;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import seng302.Model.Clinician;

public class UndoClinicianTest {

  private Clinician testUser;

  @Before
  public void setUp() {
    testUser = new Clinician("Staff1", "password", "John", "Angus", "McGurkinshaw",
        "20 Kirkwood Ave", "Caterbury");
  }

  @Test
  public void testSingleChangeSingleUndo() {

  }

  @Test
  public void testSingleChangeMultipleUndo() {

  }

  @Test
  public void testMultipleChangeMultipleUndo() {
  }

  @Test
  public void testMultipleChangesSingleUndo() {
  }

  @Test
  public void singleChangeMementoShouldContainTwoStates() {
  }

  @Test
  public void singleChangeMementoShouldContainCorrectStates() {

  }

  @Test
  public void multipleChangesConsecutiveMementosShouldShareState() {

  }

  @Test
  public void DonorAttributesAttachedUserIsCorrectWhenStored() {

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
