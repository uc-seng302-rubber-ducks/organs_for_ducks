package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import odms.App;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the UpdateClinicianController specifically for creating new clinicians
 */
public class CreateClinicianControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        clickOn("#administratorTab");
        clickOn("#adminUsernameTextField");
        write("default");
        clickOn("#adminPasswordField");
        write("admin");
        clickOn("#loginAButton");
        clickOn("#addClinicianButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
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
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOn("#confirmButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("Staff1"));
    }


    @Test
    public void testSignUpNoInfo() {
        clickOn("#confirmButton");
        verifyThat("#invalidStaffIDLabel", Node::isVisible);
        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID cannot be empty"));
        verifyThat("#emptyPasswordLabel", Node::isVisible);
        verifyThat("#emptyFNameLabel", Node::isVisible);
        verifyThat("#emptyRegionLabel", Node::isVisible);
    }

    @Test
    public void testSignUpRequiredInfoAddress() {
        interact(() -> {
            setTextField(this, "#staffIDTextField", "Staff1");
            setTextField(this, "#passwordField", "secure");
            setTextField(this, "#confirmPasswordField", "secure");
            setTextField(this, "#firstNameTextField", "Affie");
            setTextField(this, "#streetNoTextField", "76B");
            setTextField(this, "#streetNameTextField", "Cambridge St");
            setTextField(this, "#neighbourhoodTextField", "Kirkwood");
            setTextField(this, "#cityTextField", "Battlefield");
            setComboBox(this, "#regionSelector", "Otago");
            setTextField(this, "#zipCodeTextField", "8033");
            setComboBox(this, "#countrySelector", "New Zealand");
            verifyThat("#regionSelector", Node::isVisible);
            clickOnButton(this, "#confirmButton");
            verifyThat("#addressLabel", LabeledMatchers.hasText("76B Cambridge St\nKirkwood"));
            verifyThat("#cityLabel", LabeledMatchers.hasText("Battlefield"));
            verifyThat("#regionLabel", LabeledMatchers.hasText("Otago"));
            verifyThat("#countryLabel", LabeledMatchers.hasText("New Zealand"));
            verifyThat("#zipLabel", LabeledMatchers.hasText("8033"));
                });
    }

    @Test
    public void testSignUpRequiredInfoAddressNotNZ() {
        interact(() -> {
            setTextField(this, "#staffIDTextField", "Staff1");
            setTextField(this, "#passwordField", "secure");
            setTextField(this, "#confirmPasswordField", "secure");
            setTextField(this, "#firstNameTextField", "Affie");
            setTextField(this, "#streetNoTextField", "12");
            setTextField(this, "#streetNameTextField", "Choc Rd");
            setTextField(this, "#neighbourhoodTextField", "");
            setTextField(this, "#cityTextField", "Nice City");
            setTextField(this, "#zipCodeTextField", "25442232");
            setComboBox(this, "#countrySelector", "Belgium");
            setTextField(this, "#regionTextField", "Flanders");
            verifyThat("#regionTextField", Node::isVisible);
            clickOnButton(this, "#confirmButton");
            verifyThat("#addressLabel", LabeledMatchers.hasText("12 Choc Rd\n"));
            verifyThat("#cityLabel", LabeledMatchers.hasText("Nice City"));
            verifyThat("#regionLabel", LabeledMatchers.hasText("Flanders"));
            verifyThat("#countryLabel", LabeledMatchers.hasText("Belgium"));
            verifyThat("#zipLabel", LabeledMatchers.hasText("25442232"));
        });
    }

    @Test
    public void testInUseStaffID() {
        // create a new clinician
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOn("#confirmButton");
        // return to the creation screen
        clickOn("#backButton");
        clickOn("#addClinicianButton");
        // create a new clinician with the same staff ID
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        clickOn("#confirmButton");
        verifyThat("#invalidStaffIDLabel", Node::isVisible);
        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID already in use"));
    }


    @Test
    public void testNoPasswordConfirmation() {
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOn("#confirmButton");
        verifyThat("#emptyPasswordLabel", Node::isVisible);
    }


    @Test
    public void testWrongPasswordConfirmation() {
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("not secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOn("#confirmButton");
        verifyThat("#incorrectPasswordLabel", Node::isVisible);
    }


    @Test
    public void testLabelsMatch() {
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#middleNameTextField").queryAs(TextField.class).setText("Ali");
        lookup("#lastNameTextField").queryAs(TextField.class).setText("Al");
        lookup("#regionTextField").queryAs(TextField.class).setText("Canterbury");
        clickOn("#confirmButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("Staff1"));
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Affie"));
        verifyThat("#mNameLabel", LabeledMatchers.hasText("Ali"));
        verifyThat("#lNameLabel", LabeledMatchers.hasText("Al"));
        verifyThat("#regionLabel", LabeledMatchers.hasText("Canterbury"));
    }

}