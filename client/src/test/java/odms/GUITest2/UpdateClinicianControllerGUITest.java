package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.exception.ApiException;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.Clinician;
import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ContactDetails;
import odms.controller.AppController;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.UserBridge;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.*;
import static odms.TestUtils.FxRobotHelper.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
    public void setUpCreateScene() throws TimeoutException, IOException {

        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = mock(AppController.class);
        Clinician c = new Clinician("Staff1", "secure", "Affie", "Ali", "Al");
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");

        AppController.setInstance(application);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(c);
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(testUser)));
        when(bridge.getUser("ABC1244")).thenReturn(testUser);

        Address workAddress = new Address("20", "Kirkwood Ave", "",
                "Christchurch", "Canterbury", "", "");
        c.setWorkContactDetails(new ContactDetails("", "", workAddress, ""));
        AppController.getInstance().getClinicians().add(c);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
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
        clickOn("#fileMenu");
        clickOn("#logoutMenu");
        AppController.setInstance(null);
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

    @Test
    public void testUpdateRegionAndCountry() {
        interact(() -> {
            setComboBox(this, "#countrySelector", "New Zealand");
            setComboBox(this, "#regionSelector", "Otago");


        });
        verifyThat("#regionSelector", Node::isVisible);
        interact(() -> {
            clickOnButton(this, "#confirmButton");
        });
        verifyThat("#regionLabel", LabeledMatchers.hasText("Otago"));
        verifyThat("#countryLabel", LabeledMatchers.hasText("New Zealand"));
    }

    @Test
    public void testUpdateRegionAndCountryNotNZ() {
        interact(() -> {
            setComboBox(this, "#countrySelector", "Belgium");
            setTextField(this, "#regionTextField", "Flanders");

        });
        verifyThat("#regionTextField", Node::isVisible);

        interact(() -> {
            clickOnButton(this, "#confirmButton");
        });
        verifyThat("#regionLabel", LabeledMatchers.hasText("Flanders"));
        verifyThat("#countryLabel", LabeledMatchers.hasText("Belgium"));
    }
}
