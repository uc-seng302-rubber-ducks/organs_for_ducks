package seng302.Controller;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.SHORTCUT;
import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;

import java.util.concurrent.TimeoutException;
import seng302.Model.User;

public class UpdateDonorControllerTest extends ApplicationTest {
    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(new User("A", LocalDate.now().minusDays(1000), "ABC1234"));
        clickOn("#userIDTextField");
        write("ABC1234");
        clickOn("#loginButton");
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
        write("Kate",0);
        clickOn("#cancelButton");
        clickOn("Yes");
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
        write("03/05/2018",  0);
        clickOn("#confirmButton");
        verifyThat("#DOBValue", LabeledMatchers.hasText("2018-05-03"));
    }

    @Test
    public void testUpdateDoD() {
        clickOn("#dodInput");
        write("03/05/2018",  0);
        clickOn("#confirmButton");
        verifyThat("#DODValue", LabeledMatchers.hasText("2018-05-03"));
    }

    @Test
    public void testUpdateAddress() {
        clickOn("#addressInput");
        write("dkgfdjhb",  0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pAddress", LabeledMatchers.hasText("dkgfdjhb"));
    }

    @Test
    public void testUpdateHomePhone() {
        clickOn("#phoneInput");
        write("035659768",  0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("035659768"));
    }

    @Test
    public void testUpdateCellPhone() {
        clickOn("#cellInput");
        write("0224567895",  0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224567895"));
    }

    @Test
    public void testUpdateEmail() {
        clickOn("#addressInput");
        write("catface@gmail.com",  0);
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pAddress", LabeledMatchers.hasText("catface@gmail.com"));
    }

    @Test
    public void testUpdateRegion() {
        clickOn("#regionInput");
        write("catface@gmail.com",  0);
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
        write("1.75",  10);
        clickOn("#confirmButton");
        verifyThat("#heightValue", LabeledMatchers.hasText("1.75"));
    }

    @Test
    @Ignore
    public void testUpdateWeight() {
        doubleClickOn("#weightInput");
        write("65",  0);
        clickOn("#confirmButton");
        verifyThat("#weightValue", LabeledMatchers.hasText("65.0"));
    }

    @Test
    @Ignore
    public void updateBMIAfterUpdate() {
        clickOn("#heightInput");
        write("1.75",  0);
        clickOn("#weightInput");
        write("65",  0);
        clickOn("#confirmButton");
        verifyThat("#bmiValue", LabeledMatchers.hasText("21.22"));
    }

}
