package seng302.GUITest2;

import odms.App;
import odms.controller.AppController;
import odms.model.User;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.Utils.CommonTestMethods;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UpdateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(new User("A", LocalDate.now().minusDays(1000), "ABC1234"));
        clickOn("#userIDTextField");
        write("ABC1234");
        clickOn("#loginUButton");
        clickOn("#editDetailsButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void testUpdateName() {
        clickOn("#fNameInput").push(SHORTCUT, A).push(BACK_SPACE);
        clickOn("#fNameInput");
        write("");
        write("Kate");
        clickOn("#confirmButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("Kate"));
    }

    @Test
    public void testCancel() {
        clickOn("#fNameInput");
        write("Kate", 0);
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#fNameValue", LabeledMatchers.hasText("A"));
    }

    @Test
    public void testUpdateNothing() {
        clickOn("#confirmButton");
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
        clickOn("#address");
        write("dkgfdjhb", 0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pAddress", LabeledMatchers.hasText("dkgfdjhb"));
    }

    @Test
    public void testUpdateHomePhone() {
        clickOn("#phone");
        write("033572996", 0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("033572996"));
    }

    @Test
    public void testUpdateCellPhone() {
        clickOn("#cell");
        write("0224567895", 0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224567895"));
    }

    @Test
    public void testUpdateEmail() {
        clickOn("#email");
        write("catface@gmail.com", 0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pEmail", LabeledMatchers.hasText("catface@gmail.com"));
    }

    @Test
    @Ignore
    public void testUpdateRegion() {
        clickOn("#region");
        write("catface@gmail.com", 0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pRegion", LabeledMatchers.hasText("catface@gmail.com"));
    }

    @Test
    public void testUpdateBloodType() {
        clickOn("#bloodComboBox");
        clickOn("B+");
        clickOn("#confirmButton");
        verifyThat("#bloodTypeValue", LabeledMatchers.hasText("B+"));
    }

    @Test
    public void testUpdateSmokerStatus() {
        clickOn("#smokerCheckBox");
        clickOn("#confirmButton");
        verifyThat("#smokerValue", LabeledMatchers.hasText("Yes"));
    }

    @Test
    public void testUpdateAlcoholConsumption() {
        clickOn("#alcoholComboBox");
        clickOn("Low");
        clickOn("#confirmButton");
        verifyThat("#alcoholValue", LabeledMatchers.hasText("Low"));
    }

    @Test
    @Ignore
    public void testUpdateHeight() {
        doubleClickOn("#heightInput");
        write("1.75", 10);
        clickOn("#confirmButton");
        verifyThat("#heightValue", LabeledMatchers.hasText("1.75"));
    }

    @Test
    public void testUpdateWeight() {
        doubleClickOn("#weightInput");
        write("65", 0);
        clickOn("#confirmButton");
        verifyThat("#weightValue", LabeledMatchers.hasText("65.0"));
    }

    @Test
    public void updateBMIAfterUpdate() {
        clickOn("#heightInput");
        write("1.75", 0);
        clickOn("#weightInput");
        write("65", 0);
        clickOn("#confirmButton");
        verifyThat("#bmiValue", LabeledMatchers.hasText("21.22"));
    }

}
