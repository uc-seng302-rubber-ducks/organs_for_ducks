package odms.GUITest2;

import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.Clinician;
import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ContactDetails;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

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
        Clinician c = new Clinician("Staff1", "secure", "Affie", "Ali", "Al");
        Address workAddress = new Address("20", "Kirkwood Ave", "", "Christchurch", "Canterbury", "", "");
        c.setWorkContactDetails(new ContactDetails("", "", workAddress, ""));
        AppController.getInstance().getClinicians().add(c);
        clickOn("#clinicianTab");

        clickOn("#staffIdTextField");
        write("Staff1");
        clickOn("#staffPasswordField");
        write("secure");
        clickOn("#loginCButton");
        clickOn("#editMenuClinician");
        clickOn("#editDetailsClinician");
    }

    @After
    public void tearDown() throws TimeoutException {
        clickOn("#fileMenuClinician");
        clickOn("#logoutMenuClinician");
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
        verifyThat("#streetNameTextField", TextInputControlMatchers.hasText("Kirkwood Ave"));
        verifyThat("#regionTextField", TextInputControlMatchers.hasText("Canterbury"));
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
