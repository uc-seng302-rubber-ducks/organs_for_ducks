package odms.GUITest2;

import javafx.scene.Node;
import javafx.scene.control.Button;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.UserBridge;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UndoDonorGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {
        AppController application = AppControllerMocker.getFullMock();
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("a", "", "01556677"));
        user.getUndoStack().clear();
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);
        when(application.getName()).thenReturn("Jeff");
        when(application.getUsername()).thenReturn("erson");

        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(user);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");


        setTextField(this, "#userIDTextField", "ABC1234");
        clickOnButton(this, "#loginUButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
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
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#lNameInput", "Jefferson");
        clickOnButton(this, "#updateProfileButton");
        clickOnButton(this, "#undoButton");

        verifyThat("#lNameValue", LabeledMatchers.hasText(""));
    }

    /**
     * Test single change 3 undos, button should be disabled after one click
     */
    @Test
    public void testMultipleUndosWithoutSufficientChanges() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#lNameInput", "Jefferson");
        clickOnButton(this, "#updateProfileButton");
        clickOnButton(this, "#undoButton");

        Assert.assertTrue(lookup("#undoButton").queryAs(Button.class).isDisabled());
    }

    /**
     * Multiple changes, multiple undos
     */
    @Test
    public void testEqualChangesEqualUndos() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        //setTextField(this, "#lNameInput", "Jefferson");
        clickOn("#updateProfileButton");


        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#healthDetailsTab");
        clickOn("#genderIdComboBox");
        clickOn("Non Binary");
        clickOnButton(this, "#updateProfileButton");

        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#healthDetailsTab");
        clickOn("#smokerCheckBox");
        clickOnButton(this, "#updateProfileButton");
        clickOnButton(this, "#undoButton");
        clickOnButton(this, "#undoButton");
        clickOnButton(this, "#undoButton");

        verifyThat("#lNameValue", LabeledMatchers.hasText(""));
        verifyThat("#genderIdentityValue", LabeledMatchers.hasText(""));
        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
    }


    /**
     * Test undo a change to NHI
     */
    @Test
    public void testNHIChange() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        doubleClickOn("#nhiInput");

        write("ABD1111");
        clickOnButton(this, "#updateProfileButton");

        clickOnButton(this, "#undoButton");

        verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
    }

    //TODO: Add a test for organ changes
}
