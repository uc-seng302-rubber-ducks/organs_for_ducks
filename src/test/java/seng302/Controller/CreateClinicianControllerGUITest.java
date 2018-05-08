package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;

/**
 * Tests the UpdateClinicianController specifically for creating new clinicians
 */
public class CreateClinicianControllerGUITest extends ApplicationTest {

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
        clickOn("#changeLogin");
        clickOn("#signUpButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
      FxToolkit.cleanupStages();
    }

    @Test
    public void testClinicianSignUpFromLogin() {
        verifyThat("#titleLabel", LabeledMatchers.hasText("Create Clinician"));
        verifyThat("#confirmButton", LabeledMatchers.hasText("Create Clinician Profile"));
    }

    @Test
    public void testSignUpRequiredInfo() {
        clickOn("#staffIDTextField");
        write("Staff1", 0);
        clickOn("#passwordField");
        write("secure", 0);
        clickOn("#confirmPasswordField");
        write("secure",0);
        clickOn("#firstNameTextField");
        write("Affie", 0);
        clickOn("#regionTextField");
        write("Christchurch", 0);
        clickOn("#confirmButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("Staff1"));
    }

    @Test
    public void testSignUpNoInfo() {
        clickOn("#confirmButton");
//        verifyThat("#invalidStaffIDLabel", Node::isVisible);
//        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID cannot be empty"));
//        verifyThat("#emptyPasswordLabel", Node::isVisible);
//        verifyThat("#emptyFNameLabel", Node::isVisible);
//        verifyThat("#emptyRegionLabel", Node::isVisible);
    }

    @Test
    public void testInUseStaffID() {
        // create a new clinician
        clickOn("#staffIDTextField");
        write("Staff1", 0);
        clickOn("#passwordField");
        write("secure", 0);
        clickOn("#confirmPasswordField");
        write("secure", 0);
        clickOn("#firstNameTextField");
        write("Affie", 0);
        clickOn("#regionTextField");
        write("Christchurch", 0);
        clickOn("#confirmButton");
        // return to the creation screen
        clickOn("#logoutButton");
        clickOn("#changeLogin");
        clickOn("#signUpButton");
        // create a new clinician with the same staff ID
        clickOn("#staffIDTextField");
        write("Staff1", 0);
        clickOn("#confirmButton");
        verifyThat("#invalidStaffIDLabel", Node::isVisible);
        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID already in use"));
    }

    @Test
    public void testNoPasswordConfirmation() {
        clickOn("#staffIDTextField");
        write("Staff1", 0);
        clickOn("#passwordField");
        write("secure", 0);
        clickOn("#firstNameTextField");
        write("Affie", 0);
        clickOn("#regionTextField");
        write("Christchurch", 0);
        clickOn("#confirmButton");
        verifyThat("#incorrectPasswordLabel", Node::isVisible);
    }

    @Test
    public void testWrongPasswordConfirmation() {
        clickOn("#staffIDTextField");
        write("Staff1", 0);
        clickOn("#passwordField");
        write("secure", 0);
        clickOn("#confirmPasswordField");
        write("not secure", 0);
        clickOn("#firstNameTextField");
        write("Affie", 0);
        clickOn("#regionTextField");
        write("Christchurch", 0);
        clickOn("#confirmButton");
        verifyThat("#incorrectPasswordLabel", Node::isVisible);
    }

    @Test
    public void testLabelsMatch() {
        clickOn("#staffIDTextField");
        write("Staff1",  0);
        clickOn("#passwordField");
        write("secure",  0);
        clickOn("#confirmPasswordField");
        write("secure",  0);
        clickOn("#firstNameTextField");
        write("Affie",  0);
        clickOn("#middleNameTextField");
        write("Ali",  0);
        clickOn("#lastNameTextField");
        write("Al",  0);
        clickOn("#addressTextField");
        write("Our house, in the middle of our street",  0);
        clickOn("#regionTextField");
        write("Christchurch",  0);
        clickOn("#confirmButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("Staff1"));
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Affie"));
        verifyThat("#mNameLabel", LabeledMatchers.hasText("Ali"));
        verifyThat("#lNameLabel", LabeledMatchers.hasText("Al"));
        verifyThat("#addressLabel", LabeledMatchers.hasText("Our house, in the middle of our street"));
        verifyThat("#regionLabel", LabeledMatchers.hasText("Christchurch"));
    }

}