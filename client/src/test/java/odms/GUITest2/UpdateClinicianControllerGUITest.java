package odms.GUITest2;

import javafx.scene.Node;
import odms.App;
import odms.TestUtils.CommonTestMethods;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ContactDetails;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.ClinicianController;
import odms.controller.AppController;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.TransplantBridge;
import odms.utils.UserBridge;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static odms.TestUtils.FxRobotHelper.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the UpdateClinicianController specifically for updating existing clinicians
 */
public class UpdateClinicianControllerGUITest extends ApplicationTest {

    private Collection<UserOverview> overviews;

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
        TransplantBridge transplantBridge = mock(TransplantBridge.class);

        Clinician c = new Clinician("Staff1", "secure", "Affie", "Ali", "Al");
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");

        overviews = Collections.singletonList(UserOverview.fromUser(testUser));

        AppController.setInstance(application);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("OMEGALUL");

        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("OMEGALUL");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(c);
        when(transplantBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), anyCollection())).thenReturn(new ArrayList<>());
        when(bridge.loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(overviews);
        when(bridge.getUser("ABC1244")).thenReturn(testUser);

        doCallRealMethod().when(application).setClinicianController(any(ClinicianController.class));
        doCallRealMethod().when(application).getClinicianController();

        Address workAddress = new Address("20", "Kirkwood Ave", "",
                "Christchurch", "Canterbury", "", "");
        c.setWorkContactDetails(new ContactDetails("", "", workAddress, ""));

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        clickOn("#clinicianTab");

        setTextField(this, "#staffIdTextField", "Staff1");
        setTextField(this, "#staffPasswordField", "secure");
        clickOnButton(this, "#loginCButton");
        clickOn("#editButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testEditFromClinician() {
        verifyThat("#titleLabel", LabeledMatchers.hasText("Update Clinician"));
        verifyThat("#confirmButton", LabeledMatchers.hasText("Save Changes"));
        clickOnButton(this,"#cancelButton");
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
        setTextField(this, "#firstNameTextField", "Not Affie");
        clickOnButton(this, "#confirmButton");
        verifyThat("#fNameLabel", LabeledMatchers.hasText("Not Affie"));
    }

    @Test
    public void testCancelChanges() {
        setTextField(this, "#firstNameTextField", "Not Affie");
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
