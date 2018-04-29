package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.EmergencyContact;
import seng302.Model.User;

public class UndoDonorTest extends ApplicationTest {

  @Before
  public void setUp() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    User user = new User("A", LocalDate.now(), "ABC1234");
    user.setFirstName("Adam");
    user.setLastName("");
    user.setContact(new EmergencyContact("", ""));
    user.getUndoStack().clear();
    AppController.getInstance().getUsers().add(user);
    clickOn("#userIDTextField");
    write("ABC1234", 0);
    clickOn("#loginButton");
  }

  @After
  public void tearDown() throws TimeoutException {
    AppController.getInstance().getUsers().clear();
    FxToolkit.cleanupStages();
  }

  /**
   * Verifies that the undo stack is empty on startup.
   */
  @Test
  public void testUndoStackEmpty() {
    verifyThat("#undoButton", Node::isDisabled);
  }

  /**
   * Test single change single undo
   */
  @Test
  public void testSingleUndo() {
    clickOn("#editDetailsButton");
    clickOn("#lNameInput");
    write("Jefferson");
    clickOn("#confirmButton");
    clickOn("#undoButton");

    verifyThat("#lNameValue", LabeledMatchers.hasText(""));
  }

  /**
   * Test single change 3 undos, button should be disabled after one click
   */
  @Test
  public void testMultipleUndosWithoutSufficientChanges() {
    clickOn("#editDetailsButton");
    clickOn("#lNameInput");
    write("Jefferson");
    clickOn("#confirmButton");
    clickOn("#undoButton");

    verifyThat("#undoButton", Node::isDisabled);
  }

  /**
   * Multiple changes, multiple undos
   */
  @Test
  public void testEqualChangesEqualUndos() {

    clickOn("#editDetailsButton");
    clickOn("#lNameInput");
    write("Jefferson");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#genderIdComboBox");
    clickOn("Non Binary");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#smokerCheckBox");
    clickOn("#confirmButton");

    clickOn("#undoButton");

    clickOn("#undoButton");

    clickOn("#undoButton");

    verifyThat("#lNameValue", LabeledMatchers.hasText(""));
    verifyThat("#genderIdentityValue", LabeledMatchers.hasText(""));
    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
  }

  /**
   * Multiple changes, single undo
   */
  @Test
  public void testMultipleChangesSingleUndo() {
    clickOn("#editDetailsButton");
    clickOn("#lNameInput");
    write("Jefferson");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#genderIdComboBox");
    clickOn("Non Binary");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#smokerCheckBox");
    clickOn("#confirmButton");
    clickOn("#undoButton");

    verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    verifyThat("#genderIdentityValue", LabeledMatchers.hasText("Non Binary"));
    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
  }

  /**
   * 3 changes, 1 undo, 2 changes, 3 undos, result should be one step from base
   */
  @Test
  public void test3Changes1Undo2Changes3Undos() {
    clickOn("#editDetailsButton");
    clickOn("#lNameInput");
    write("Jefferson");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#genderIdComboBox");
    clickOn("Non Binary");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#smokerCheckBox");
    clickOn("#confirmButton");

    clickOn("#undoButton");

    clickOn("#editDetailsButton");
    clickOn("#mNameInput");
    write("John");
    clickOn("#confirmButton");

    clickOn("#editDetailsButton");
    clickOn("#cellInput");
    write("0800838383");
    clickOn("#confirmButton");

    clickOn("#undoButton");
    clickOn("#undoButton");
    clickOn("#undoButton");

    verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    verifyThat("#genderIdentityValue", LabeledMatchers.hasText(""));
    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
    verifyThat("#mNameValue", LabeledMatchers.hasText(""));
    verifyThat("#pCellPhone", LabeledMatchers.hasText(""));
  }

  /**
   * Test undo a change to NHI
   */
  @Test
  public void testNHIChange() {
    clickOn("#editDetailsButton");
    doubleClickOn("#nhiInput");

    write("ABD1111");
    clickOn("#confirmButton");

    clickOn("#undoButton");

    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  //TODO: Add a test for organ changes
}
