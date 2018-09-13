package odms.GUITest2;

import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
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

import static odms.TestUtils.FxRobotHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UpdateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {


        AppController application = AppControllerMocker.getFullMock();
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("A", LocalDate.now().minusDays(1000), "ABC1234");
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

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(user);

        setTextField(this,"#userIDTextField", "ABC1234");
        clickOnButton(this,"#loginUButton");
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testUpdateName() {
        setTextField(this, "#fNameInput","");
        setTextField(this, "#fNameInput","Kate");
        clickOnButton(this, "#confirmButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("Kate"));
    }

    @Test
    public void testUpdateNothing() {
        clickOnButton(this,"#confirmButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("A"));
    }

    @Test
    public void testUpdateDoB() {
        setDateValue(this, "#dobInput", LocalDate.of(2018, 5, 3));
        clickOnButton(this,"#confirmButton");
        verifyThat("#DOBValue", LabeledMatchers.hasText(LocalDate.of(2018, 5, 3).toString()));
    }

    @Test
    public void testUpdateHomePhone() {
        setTextField(this,"#phone","033572996");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("033572996"));
    }

    @Test
    public void testUpdateCellPhone() {
        setTextField(this,"#cell","0224567895");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224567895"));
    }

    @Test
    public void testUpdateEmail() {
        setTextField(this,"#email","catface@gmail.com");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pEmail", LabeledMatchers.hasText("catface@gmail.com"));
    }

    @Test
    public void testUpdateBloodType() {
        clickOn("#bloodComboBox");
        clickOn("B+");
        clickOnButton(this,"#confirmButton");
        verifyThat("#bloodTypeValue", LabeledMatchers.hasText("B+"));
    }

    @Test
    public void testUpdateSmokerStatus() {
        clickOn("#smokerCheckBox");
        clickOnButton(this,"#confirmButton");
        verifyThat("#smokerValue", LabeledMatchers.hasText("Yes"));
    }

    @Test
    public void testUpdateAlcoholConsumption() {
        clickOn("#alcoholComboBox");
        clickOn("Low");
        clickOnButton(this,"#confirmButton");
        verifyThat("#alcoholValue", LabeledMatchers.hasText("Low"));
    }

    @Test
    public void testUpdateWeight() {
        setTextField(this,"#weightInput","65");
        clickOnButton(this,"#confirmButton");
        verifyThat("#weightValue", LabeledMatchers.hasText("65.0"));
    }

    @Test
    public void updateBMIAfterUpdate() {
        setTextField(this,"#heightInput","175");
        setTextField(this,"#weightInput","65");
        clickOnButton(this,"#confirmButton");
        verifyThat("#bmiValue", LabeledMatchers.hasText("21.22"));
    }

}
