package seng302.Controller;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
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
    fail("not yet implemented");
  }

  @Test
  public void testSingleChangeMultipleUndo() {
    fail("not yet implemented");
  }

  @Test
  public void testMultipleChangeMultipleUndo() {
    fail("not yet implemented");
  }

  @Test
  public void testMultipleChangesSingleUndo() {
    fail("not yet implemented");
  }

  @Test
  public void singleChangeMementoShouldContainTwoStates() {
    //change first name
    //memento should have user with old and new first name
    fail("not yet implemented");
  }

  @Test
  public void singleChangeMementoShouldContainCorrectStates() {
    fail("not yet implemented");
  }

  @Test
  public void multipleChangesConsecutiveMementosShouldShareState() {
    //state after one change should be the state before the next change
    fail("not yet implemented");
  }

  @Test
  public void DonorAttributesAttachedUserIsCorrectAfterUndoneChange() {
    //DonorAttributes.attachedUser() should be testUser at all times
    //change nhi as this is what user.equals works on
    fail("not yet implemented");
  }

  @Test
  public void ReceiverAttributesAttachedUserIsCorrectAfterUndoneChange() {
    fail("not yet implemented");
  }
}
