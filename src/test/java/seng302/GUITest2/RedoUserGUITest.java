package seng302.GUITest2;

import javafx.scene.Node;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Utils.CommonTestMethods;
import seng302.controller.AppController;
import seng302.model.EmergencyContact;
import seng302.model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

@Ignore
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
        User user = new User("ABC1234", LocalDate.now().minusDays(2), LocalDate.now(), "", "", "", "", "",
                null,
                "Adam", "Adam", "Adam", "", "");
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
    @Ignore
    public void testRedoButtonDisabled() {
        verifyThat("#redoButton", Node::isDisabled);
    }

    @Test
    @Ignore
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
    @Ignore
    public void testRedoEqualUndos() {
        clickOn("#editDetailsButton");
        clickOn("#lNameInput");
        write("Jefferson");

        clickOn("#alcoholComboBox");
        clickOn("Low");

        clickOn("#cellInput");
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
