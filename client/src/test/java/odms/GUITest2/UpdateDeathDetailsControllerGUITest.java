package odms.GUITest2;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.ClinicianBridge;
import odms.bridge.LoginBridge;
import odms.bridge.TransplantBridge;
import odms.bridge.UserBridge;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static odms.TestUtils.TableViewsMethod.getCell;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UpdateDeathDetailsControllerGUITest extends ApplicationTest{

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");
    private Collection<UserOverview> overviews = Collections.singletonList(UserOverview.fromUser(testUser));

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {

        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = AppControllerMocker.getFullMock();
        TransplantBridge transplantBridge = mock(TransplantBridge.class);
        UserController userController = mock(UserController.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        AppController.setInstance(application);
        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("ahaahahahahhaha");
        doNothing().when(userController).showUser(any(User.class));

        when(application.getTransplantList()).thenReturn(new ArrayList<>());
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        doNothing().when(application).addUserOverview(any(UserOverview.class));

        when(application.getUserOverviews()).thenReturn(new HashSet<>(overviews));
        when(bridge.getUser("ABC1244")).thenReturn(testUser);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);


    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    private void loginAsClinician() {
        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField" ,"0");
        setTextField(this, "#staffPasswordField","admin");
        clickOnButton(this,"#loginCButton");
        clickOn("#searchTab");
        interact(() -> {
            lookup("#searchTableView").queryAs(TableView.class).setItems(FXCollections.observableList(Collections.singletonList(UserOverview.fromUser(testUser))));
            lookup("#searchTableView").queryAs(TableView.class).refresh();
        });
        doubleClickOn(getCell("#searchTableView", 0, 0));
    }

    @Test
    public void testUserCannotEditDeathDetails() {
        setTextField(this,"#userIDTextField", "ABC1244");
        clickOnButton(this,"#loginUButton");
        verifyThat("#updateDeathDetailsButton", Node::isDisabled);
    }

    @Test
    public void testDateOfDeathCannotBeAfterCurrentDay() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now().plusDays(1));
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45"); // Make sure time doesn't through an error
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("There is an error with your Date of Death"));
    }

    @Test
    public void testDateOfDeathCannotBeBeforeBirthDate() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", testUser.getDateOfBirth().minusDays(1));
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("There is an error with your Date of Death"));
    }

    @Test
    public void testTimeOfDeathCannotBeInvalid() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now()); //Make sure date is not invalid
        //Doing multiple in one test to speed up tests
        setTextField(this, "#updateDeathDetailsTimeTextField", "12:30pm");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("The format of the Time of Death is incorrect"));

        setTextField(this, "#updateDeathDetailsTimeTextField", "24:00");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("The format of the Time of Death is incorrect"));

        setTextField(this, "#updateDeathDetailsTimeTextField", "23:60");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("The format of the Time of Death is incorrect"));
    }

    @Test
    public void testOverviewUpdatesWhenConfirmClicked() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45");
        setTextField(this, "#updateDeathDetailsCityTextField", "Atlantis");
        setTextField(this, "#updateDeathDetailsRegionTextField", "Atlantic");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");

        verifyThat("#DODValue", LabeledMatchers.hasText(LocalDate.now().toString()));
        verifyThat("#cityOfDeathValue", LabeledMatchers.hasText("Atlantis"));
        verifyThat("#regionOfDeathValue", LabeledMatchers.hasText("Atlantic"));
        //verifyThat("countryOfDeathValue", LabeledMatchers.hasText("Afghanistan"));
    }

    @Test
    public void testOverviewUpdatesWhenCancelClicked() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45");
        setTextField(this, "#updateDeathDetailsCityTextField", "Atlantis");
        setTextField(this, "#updateDeathDetailsRegionTextField", "Atlantic");
        clickOnButton(this, "#cancelUpdateDeathDetailsButton");

        verifyThat("#DODValue", LabeledMatchers.hasText(""));
        verifyThat("#cityOfDeathValue", LabeledMatchers.hasText(""));
        verifyThat("#regionOfDeathValue", LabeledMatchers.hasText(""));


    }

}
