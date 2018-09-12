package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * Tests the UpdateClinicianController specifically for creating new clinicians
 */
public class CreateClinicianControllerGUITest extends ApplicationTest {

    private AppController application = AppControllerMocker.getFullMock();
    private UserBridge bridge = mock(UserBridge.class);
    private ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private AdministratorBridge administratorBridge  = mock(AdministratorBridge.class);
    private LoginBridge loginBridge = mock(LoginBridge.class);
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);
    private CountriesBridge countriesBridge = mock(CountriesBridge.class);
    private OrgansBridge organsBridge = mock(OrgansBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        AppController.setInstance(application);
        when(application.getCountriesBridge()).thenReturn(countriesBridge);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getAdministratorBridge()).thenReturn(administratorBridge);
        when(application.getCountriesBridge()).thenReturn(countriesBridge);
        when(application.getOrgansBridge()).thenReturn(organsBridge);
        Set<String> countries = new HashSet<>();
        countries.add("New Zealand");
        when(countriesBridge.getAllowedCountries()).thenReturn(countries);
        when(application.getTransplantList()).thenReturn(new ArrayList<>());
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        doNothing().when(organsBridge).getAvailableOrgansList(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), any(), null);
        when(application.getToken()).thenReturn("fakeToken");
        when(administratorBridge.getAdmin(anyString(), anyString())).thenReturn(new Administrator("default", "", "", "", ""));


        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        clickOn("#administratorTab");
        setTextField(this, "#adminUsernameTextField", "default");
        setTextField(this, "#adminPasswordField", "admin");
        clickOnButton(this,"#loginAButton");
        clickOnButton(this,"#addClinicianButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getClinicians().remove(AppController.getInstance().getClinician("Staff1"));
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }


    @Test
    public void testClinicianSignUpFromLogin() {
        verifyThat("#titleLabel", LabeledMatchers.hasText("Create Clinician"));
        verifyThat("#confirmButton", LabeledMatchers.hasText("Create Clinician Profile"));
    }

    @Test
    public void testSignUpNoInfo() {
        clickOnButton(this, "#confirmButton");
        verifyThat("#invalidStaffIDLabel", Node::isVisible);
        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID cannot be empty"));
        verifyThat("#emptyPasswordLabel", Node::isVisible);
        verifyThat("#emptyFNameLabel", Node::isVisible);
    }

    @Test
    public void testInUseStaffID() {
        // create a new clinician
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        setComboBox(this, "#countrySelector", "New Zealand");
        setComboBox(this, "#regionSelector", "Christchurch");
        clickOnButton(this,"#confirmButton");
        // return to the creation screen
        clickOn("#fileMenuClinician");
        clickOn("#logoutMenuClinician");
        clickOnButton(this,"#addClinicianButton");
        when(application.getClinician(anyString())).thenReturn(new Clinician("Affie", "Staff1", "any"));
        // create a new clinician with the same staff ID
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        setTextField(this, "#passwordField", "secure");
        setTextField(this, "#confirmPasswordField", "secure");
        setTextField(this, "#firstNameTextField", "Addie");
        setTextField(this, "#regionTextField", "Wellington");
        clickOnButton(this,"#confirmButton");
        verifyThat("#invalidStaffIDLabel", Node::isVisible);
        verifyThat("#invalidStaffIDLabel", LabeledMatchers.hasText("Staff ID already in use"));
    }


    @Test
    public void testNoPasswordConfirmation() {
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOnButton(this,"#confirmButton");
        verifyThat("#emptyPasswordLabel", Node::isVisible);
    }


    @Test
    public void testWrongPasswordConfirmation() {
        lookup("#staffIDTextField").queryAs(TextField.class).setText("Staff1");
        lookup("#passwordField").queryAs(TextField.class).setText("secure");
        lookup("#confirmPasswordField").queryAs(TextField.class).setText("not secure");
        lookup("#firstNameTextField").queryAs(TextField.class).setText("Affie");
        lookup("#regionTextField").queryAs(TextField.class).setText("Christchurch");
        clickOnButton(this,"#confirmButton");
        verifyThat("#incorrectPasswordLabel", Node::isVisible);
    }
}