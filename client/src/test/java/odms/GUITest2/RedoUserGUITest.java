package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.commons.model.EmergencyContact;
import odms.commons.model.User;
import odms.controller.gui.window.UserController;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.UserBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class RedoUserGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {

        AppController application = mock(AppController.class);
        UserBridge bridge = mock(UserBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);

        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);

        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();

        user.setContact(new EmergencyContact("", "", "1456788"));
        user.getUndoStack().clear();

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

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
        clickOnButton(this,"#editDetailsButton");
        setTextField(this, "#lNameInput", "Jefferson");
        clickOnButton(this,"#confirmButton");
        clickOnButton(this,"#undoButton");
        clickOnButton(this,"#redoButton");
        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));
    }

    @Test
    public void testMergedRedosEqualMergedUndos() {
        clickOnButton(this,"#editDetailsButton");
        setTextField(this, "#lNameInput", "Jefferson");

        clickOn("#alcoholComboBox");
        clickOn("Low");

        setTextField(this, "#cell", "011899992");
        clickOnButton(this,"#confirmButton");

        clickOnButton(this,"#undoButton");
        clickOnButton(this,"#redoButton");

        verifyThat("#alcoholValue", LabeledMatchers.hasText("Low"));
        verifyThat("#lNameValue", LabeledMatchers.hasText("Jefferson"));

        clickOn("#detailsTab");
        clickOn("#undoButton");

        verifyThat("#pCellPhone", LabeledMatchers.hasText(""));

        clickOn("#redoButton");

        verifyThat("#pCellPhone", LabeledMatchers.hasText("011899992"));




    }
}
