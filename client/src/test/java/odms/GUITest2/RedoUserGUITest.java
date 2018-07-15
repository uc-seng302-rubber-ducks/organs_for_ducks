package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.controller.AppController;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class RedoUserGUITest extends ApplicationTest {

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
        user.setContact(new EmergencyContact("", "", "1456788"));
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
    public void testRedoButtonDisabled() {
        verifyThat("#redoButton", Node::isDisabled);
    }

    @Test
    public void testRedoSingleUndo() {
        clickOn("#editDetailsButton");
        clickOn("#lNameInput");
        write("Jefferson");
        clickOn("#confirmButton");
        clickOn("#undoButton");
        clickOn("#redoButton");

        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    }

    @Test
    public void testRedoEqualUndos() {
        clickOn("#editDetailsButton");
        clickOn("#lNameInput");
        write("Jefferson");

        clickOn("#alcoholComboBox");
        clickOn("Low");

        clickOn("#cell");
        write("011899992");
        clickOn("#confirmButton");

        clickOn("#undoButton");
        clickOn("#undoButton");
        clickOn("#undoButton");

        clickOn("#redoButton");
        clickOn("#redoButton");
        clickOn("#redoButton");

        verifyThat("#pCellPhone", LabeledMatchers.hasText("011899992"));
        verifyThat("#alcoholValue", LabeledMatchers.hasText("Low"));
        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    }
}
