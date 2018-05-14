package seng302.GUITests;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.BACK_SPACE;
import static javafx.scene.input.KeyCode.SHORTCUT;
import static org.testfx.api.FxAssert.verifyThat;

import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.Clinician;

/**
 * Tests the UpdateClinicianController specifically for updating existing clinicians
 */
public class UpdateClinicianControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1920x1080-32");
        }
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        AppController.getInstance().getClinicians().add(new Clinician("Staff1", "secure", "Affie", "Ali", "Al", "20 Kirkwood Ave", "Christchurch"));
        clickOn("#changeLogin");

        clickOn("#userIDTextField");
        write("Staff1");
        clickOn("#passwordField");
        write("secure");
        clickOn("#loginButton");
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