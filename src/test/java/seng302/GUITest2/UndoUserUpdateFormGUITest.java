package seng302.GUITest2;

import javafx.scene.Node;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import seng302.App;
import seng302.Utils.CommonTestMethods;
import seng302.controller.AppController;
import seng302.model.EmergencyContact;
import seng302.model.User;

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
    @Ignore
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
    @Ignore
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
    @Ignore
    public void MultipleChangesSingleUndo() {
        clickOn("#editDetailsButton");
        clickOn("#ecPhone");
        write("1234");

        clickOn("#undoUpdateButton");

        verifyThat("#ecPhone", TextInputControlMatchers.hasText("123"));
    }

    @Test
    @Ignore
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
