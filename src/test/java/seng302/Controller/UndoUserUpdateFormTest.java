package seng302.Controller;

import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import seng302.App;
import seng302.Model.EmergencyContact;
import seng302.Model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class UndoUserUpdateFormTest extends ApplicationTest{

  @Before
  public void setUp() throws TimeoutException{
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    User user = new User("ABC1234", LocalDate.now().minusDays(2), LocalDate.now(), "", "Non Binary", 0, 0,
            "B-", "None", false, "", "", "", "", "",
            null,
            "Frank", "Frank", "Frank", "", "");
    user.setContact(new EmergencyContact("", "", user));
    user.getUndoStack().clear();
    AppController.getInstance().getUsers().add(user);
    clickOn("#userIDTextField");
    write("ABC1234", 0);
    clickOn("#loginUButton");
  }

  @After
  public void tearDown() throws TimeoutException {
    AppController.getInstance().getUsers().clear();
    FxToolkit.cleanupStages();
  }

  @Test
  public void SingleChangeSingleUndo() {
    clickOn("#editDetailsButton");
    clickOn("#preferredFNameTextField");
    write("i", 0);

    clickOn("#undoUpdateButton");
    verifyThat("#preferredFNameTextField", TextInputControlMatchers.hasText("Frank"));

  }

  @Test
  public void NoChangeUndoDisabled() {
    clickOn("#editDetailsButton");
    verifyThat("#undoUpdateButton", Node::isDisabled);
  }

  @Test
  public void ChangesResetWhenCancelButtonClicked() {
    clickOn("#editDetailsButton");
    clickOn("#mNameInput");
    write("geoff");
    clickOn("#smokerCheckBox");

    clickOn("#cancelButton");
    clickOn("#yesButton");

    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
    verifyThat("#mNameValue", LabeledMatchers.hasText(""));
  }

  @Test
  public void ChangesResetWhenWindowClosed() {
    //by clicking the X
    //TODO unsure how to use system controls from testFX
  }

  @Test
  public void MultipleChangesSummedInMainWindow() {
    clickOn("#editDetailsButton");
    clickOn("#mNameInput");
    write("geoff");
    clickOn("#smokerCheckBox");
    clickOn("#confirmButton");

    verifyThat("#smokerValue", LabeledMatchers.hasText("Yes"));
    verifyThat("#mNameValue", LabeledMatchers.hasText("geoff"));

    clickOn("#undoButton");

    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
    verifyThat("#mNameValue", LabeledMatchers.hasText(""));
  }

  @Test
  public void MultipleChangesSingleUndo() {
    clickOn("#editDetailsButton");
    clickOn("#ecPhoneInput");
    write("1234");

    clickOn("#undoUpdateButton");

    verifyThat("#ecPhoneInput", TextInputControlMatchers.hasText("123"));
  }

  @Test
  public void MultipleChangesEqualUndos() {
    clickOn("#editDetailsButton");

//    unable to check text in combo boxes as it is lazily created/populated
//    clickOn("#genderIdComboBox");
//    clickOn("Male");

    doubleClickOn("#heightInput");
    write("1");

    clickOn("#lNameInput");
    write("qw");

    clickOn("#undoUpdateButton");
    clickOn("#undoUpdateButton");
    clickOn("#undoUpdateButton");


    verifyThat("#lNameInput", TextInputControlMatchers.hasText(""));
    verifyThat("#heightInput", TextInputControlMatchers.hasText("0.0"));
  }

  @Test
  public void MultipleActionsTwoUndosOneAction() {
    //check we can traverse the stack properly
    clickOn("#editDetailsButton");

    doubleClickOn("#heightInput");
    write("1");

    clickOn("#lNameInput");
    write("qw");

    clickOn("#undoUpdateButton");
    clickOn("#undoUpdateButton");

    clickOn("#lNameInput");
    write("lasagna");

    verifyThat("#heightInput", TextInputControlMatchers.hasText("1"));
    verifyThat("#lNameInput", TextInputControlMatchers.hasText("lasagna"));
  }
}
