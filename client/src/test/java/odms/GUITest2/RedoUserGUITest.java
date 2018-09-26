package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.UserBridge;
import odms.commons.model.Clinician;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class RedoUserGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {

        AppController application = AppControllerMocker.getFullMock();
        UserBridge bridge = mock(UserBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getUsername()).thenReturn("asfdsafsafsa");

        when(application.getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);

        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();

        user.setContact(new EmergencyContact("", "", ""));
        user.getUndoStack().clear();

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(user);

        setTextField(this, "#userIDTextField","ABC1234");
        clickOnButton(this, "#loginUButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testRedoButtonDisabled() {
        verifyThat("#redoButton", Node::isDisabled);
    }

    @Test
    public void testRedoSingleUndo() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#lNameInput", "Jefferson");
        clickOnButton(this,"#updateProfileButton");
        clickOnButton(this,"#undoButton");
        clickOnButton(this,"#redoButton");
        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    }

    @Test
    public void testMergedRedosEqualMergedUndos() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");

        setTextField(this, "#lNameInput", "Jefferson");
        clickOnButton(this,"#updateProfileButton");

        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");;
        setComboBox(this, "#alcoholComboBox", "Low");
        clickOnButton(this,"#updateProfileButton");

        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#cell", "011899992");
        clickOnButton(this,"#updateProfileButton");

        clickOnButton(this,"#undoButton");
        clickOnButton(this,"#redoButton");

        verifyThat("#alcoholValue", LabeledMatchers.hasText("Low"));
        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));

        clickOn("#detailsTab");
        clickOnButton(this,"#undoButton");

        verifyThat("#pCellPhone", LabeledMatchers.hasText(""));

        clickOnButton(this,"#redoButton");

        verifyThat("#pCellPhone", LabeledMatchers.hasText("011899992"));




    }
}
