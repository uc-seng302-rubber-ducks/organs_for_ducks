package odms.GUITest2;

import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.bridge.UserBridge;
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

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UndoUserUpdateFormGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runMethods();
    }

    @Before
    public void setUp() throws TimeoutException, IOException {

        AppController application = AppControllerMocker.getFullMock();
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("Frank", LocalDate.now().minusDays(2), "ABC1234");
        user.setPreferredFirstName("Frank");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("", "", ""));
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

    @Test
    public void changesResetWhenCancelButtonClicked() {
        //Dont change me to the new methods ill break
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#mNameInput");
        write("geoff");
        clickOn("#healthDetailsTab");
        clickOn("#smokerCheckBox");

        clickOn("#UserCancelButton");
        clickOn("#yesButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
        verifyThat("#mNameValue", LabeledMatchers.hasText(""));
    }


    @Test
    public void multipleChangesNotSummedInMainWindow() {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        setTextField(this, "#mNameInput", "geoff");
        clickOn("#healthDetailsTab");
        clickOn("#smokerCheckBox");
        clickOnButton(this, "#updateProfileButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("Yes"));
        verifyThat("#mNameValue", LabeledMatchers.hasText("geoff"));

        clickOnButton(this, "#undoButton");

        verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
        verifyThat("#mNameValue", LabeledMatchers.hasText("geoff"));
    }
}
