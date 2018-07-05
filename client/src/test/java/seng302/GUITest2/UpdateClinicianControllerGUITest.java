package seng302.GUITest2;

import odms.App;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import seng302.TestUtils.CommonTestMethods;

import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the UpdateClinicianController specifically for updating existing clinicians
 */
public class UpdateClinicianControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        AppController.getInstance().getClinicians().add(new Clinician("Staff1", "secure", "Affie", "Ali", "Al", "20 Kirkwood Ave", "Christchurch"));
        clickOn("#clinicianTab");

        clickOn("#staffIdTextField");
        write("Staff1");
        clickOn("#staffPasswordField");
        write("secure");
        clickOn("#loginCButton");
        clickOn("#editButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        clickOn("#logoutButton");
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        FxToolkit.cleanupStages();
    }

    @Test
    public void testEditFromClinician() {
        verifyThat("#titleLabel", LabeledMatchers.hasText("Update Clinician"));
        verifyThat("#confirmButton", LabeledMatchers.hasText("Save Changes"));
        clickOn("#cancelButton");
    }

    @Test
    public void testTextFieldsPreloaded() {
        verifyThat("#staffIDTextField", TextInputControlMatchers.hasText("Staff1"));
        verifyThat("#firstNameTextField", TextInputControlMatchers.hasText("Affie"));
        verifyThat("#middleNameTextField", TextInputControlMatchers.hasText("Ali"));
        verifyThat("#lastNameTextField", TextInputControlMatchers.hasText("Al"));
        verifyThat("#addressTextField", TextInputControlMatchers.hasText("20 Kirkwood Ave"));
        verifyThat("#regionTextField", TextInputControlMatchers.hasText("Christchurch"));
        clickOn("#cancelButton");
    }

    @Test
    public void testChangeFirstName() {
        clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        write("Not Affie");
        clickOn("#confirmButton");
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Not Affie"));
    }

    @Test
    public void testCancelChanges() {
        clickOn("#firstNameTextField").push(SHORTCUT, A).push(BACK_SPACE);
        write("Not Affie");
        clickOn("#cancelButton");
        clickOn("#yesButton");
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Affie"));
    }
}
