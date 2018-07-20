package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class UndoUserUpdateFormGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("", "", "0187878"));
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
        clickOn("#editMenu");
        clickOn("#editDetails");
        clickOn("#preferredFNameTextField");
        write("i", 0);

        clickOn("#undoUpdateButton");
        verifyThat("#preferredFNameTextField", TextInputControlMatchers.hasText("Frank"));

    }

    @Test
    public void NoChangeUndoDisabled() {
        clickOn("#editMenu");
        clickOn("#editDetails");
        verifyThat("#undoUpdateButton", Node::isDisabled);
    }

    @Test
    public void ChangesResetWhenCancelButtonClicked() {
        clickOn("#editMenu");
        clickOn("#editDetails");
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
        clickOn("#editMenu");
        clickOn("#editDetails");
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
        clickOn("#editMenu");
        clickOn("#editDetails");
        clickOn("#ecPhone");
        write("1234");

        clickOn("#undoUpdateButton");

        verifyThat("#ecPhone", TextInputControlMatchers.hasText("123"));
    }

    @Test
    public void MultipleChangesEqualUndos() {
        clickOn("#editMenu");
        clickOn("#editDetails");

//    unable to check text in combo boxes as it is lazily created/populated
        clickOn("#genderIdComboBox");
        clickOn("Male");

        doubleClickOn("#heightInput");
        write("1");

        clickOn("#lNameInput");
        write("qw");

        clickOn("#undoUpdateButton");
        clickOn("#undoUpdateButton");
        clickOn("#undoUpdateButton");


        verifyThat("#lNameInput", TextInputControlMatchers.hasText(""));
        verifyThat("#heightInput", TextInputControlMatchers.hasText(""));
    }

    @Test
    public void MultipleActionsTwoUndosOneAction() {
        //check we can traverse the stack properly
        clickOn("#editMenu");
        clickOn("#editDetails");

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
