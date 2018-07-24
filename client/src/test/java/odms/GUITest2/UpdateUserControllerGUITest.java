package odms.GUITest2;

import odms.App;
import odms.commons.model.EmergencyContact;
import odms.commons.model.dto.UserOverview;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.utils.UserBridge;
import odms.controller.AppController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

public class UpdateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {


        AppController application = mock(AppController.class);
        UserBridge bridge = mock(UserBridge.class);

        AppController.setInstance(application);
        User user = new User("A", LocalDate.now().minusDays(1000), "ABC1234");
        user.setDateOfDeath(LocalDate.now());
        user.setContact(new EmergencyContact("", "", "0187878"));
        user.getUndoStack().clear();
        when(application.getUserBridge()).thenReturn(bridge);
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(user)));
        when(bridge.getUser("ABC1234")).thenReturn(user);


        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(user);

        setTextField(this,"#userIDTextField", "ABC1234");
        clickOnButton(this,"#loginUButton");
        clickOnButton(this,"#editMenu");
        clickOn("#editDetails");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testUpdateName() {
        clickOn("#fNameInput").push(SHORTCUT, A).push(BACK_SPACE);
        setTextField(this, "#fNameInput","");
        setTextField(this, "#fNameInput","Kate");
        clickOnButton(this, "#confirmButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("Kate"));
    }

    @Test
    public void testCancel() {
        setTextField(this, "#fNameInput","Kate");
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("A"));
    }

    @Test
    public void testUpdateNothing() {
        clickOnButton(this,"#confirmButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("A"));
    }

    @Test
    public void testUpdateDoB() {
        clickOn("#dobInput").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#dobInput");
        write("03/05/2018", 0);
        clickOn("#confirmButton");
        verifyThat("#DOBValue", LabeledMatchers.hasText("2018-05-03"));
    }

    @Test
    public void testUpdateDoD() {
        clickOn("#dodInput");
        write("03/05/2018", 0);
        clickOn("#confirmButton");
        verifyThat("#DODValue", LabeledMatchers.hasText("2018-05-03"));
    }

    @Test
    @Ignore
    public void testUpdateAddress() {
        setTextField(this,"#address","dkgfdjhb");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pAddress", LabeledMatchers.hasText("dkgfdjhb"));
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
    @Ignore
    public void testUpdateRegion() {
        setTextField(this,"#region","catface@gmail.com");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pRegion", LabeledMatchers.hasText("catface@gmail.com"));
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
    @Ignore
    public void testUpdateHeight() {
        setTextField(this,"#heightInput","1.75");
        clickOnButton(this,"#confirmButton");
        verifyThat("#heightValue", LabeledMatchers.hasText("1.75"));
    }

    @Test
    public void testUpdateWeight() {
        setTextField(this,"#weightInput","65");
        clickOnButton(this,"#confirmButton");;
        verifyThat("#weightValue", LabeledMatchers.hasText("65.0"));
    }

    @Test
    public void updateBMIAfterUpdate() {
        setTextField(this,"#heightInput","1.75");
        setTextField(this,"#weightInput","65");
        clickOnButton(this,"#confirmButton");
        verifyThat("#bmiValue", LabeledMatchers.hasText("21.22"));
    }

}
