package odms.GUITest2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ExpiryReason;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final String dateErrorText = "There is an error with your Date of Death";

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
        OrgansBridge organsBridge = mock(OrgansBridge.class);

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

        when(application.getName()).thenReturn("Jeff");
        when(application.getUsername()).thenReturn("erson");

        when(application.getOrgansBridge()).thenReturn(organsBridge);
        doNothing().when(organsBridge).getAvailableOrgansList(anyInt(),anyInt(),anyString(),anyString(),
                anyString(),anyString(),anyString(),any(ObservableList.class));

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
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText(dateErrorText));
    }

    @Test
    public void testDateOfDeathCannotBeBeforeBirthDate() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", testUser.getDateOfBirth().minusDays(1));
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText(dateErrorText));
    }

    @Test
    public void testTimeOfDeathCannotBeInvalid() {
        final String errorText = "The format of the Time of Death is incorrect";
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now()); //Make sure date is not invalid
        //Doing multiple in one test to speed up tests
        setTextField(this, "#updateDeathDetailsTimeTextField", "12:30pm");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText(errorText));

        setTextField(this, "#updateDeathDetailsTimeTextField", "24:00");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText(errorText));

        setTextField(this, "#updateDeathDetailsTimeTextField", "23:60");
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");
        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText(errorText));
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

    @Test
    public void testNoChangeWhenRemoveDeathDetailsIsCancelled() {
        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", "02:45");
        setTextField(this, "#updateDeathDetailsCityTextField", "Atlantis");
        setTextField(this, "#updateDeathDetailsRegionTextField", "Atlantic");
        clickOnButton(this, "#removeUpdateDeathDetailsButton");
        clickOnButton(this, "#cancelRemoveDeathDetailsButton");

        verifyThat("#updateDeathDetailsTimeTextField", TextInputControlMatchers.hasText("02:45"));
        verifyThat("#updateDeathDetailsCityTextField", TextInputControlMatchers.hasText("Atlantis"));
        verifyThat("#updateDeathDetailsRegionTextField", TextInputControlMatchers.hasText("Atlantic"));
    }

    @Test
    public void testOverviewUpdatesWhenDeathDetailsRemoved() {
        LocalDateTime testNow = LocalDateTime.now();
        testUser.setMomentOfDeath(testNow);
        testUser.setDeathCity("Atlantis");
        testUser.setDeathRegion("Atlantic");
        testUser.setDeathCountry("Australia");

        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        clickOnButton(this, "#removeUpdateDeathDetailsButton");
        clickOnButton(this, "#confirmRemoveDeathDetailsButton");

        verifyThat("#DODValue", LabeledMatchers.hasText(""));
        verifyThat("#cityOfDeathValue", LabeledMatchers.hasText(""));
        verifyThat("#regionOfDeathValue", LabeledMatchers.hasText(""));
        verifyThat("#countryOfDeathValue", LabeledMatchers.hasText(""));

    }

    @Test
    public void testDateAndTimeCannotBeEditedIfOrganExpired() {
        ExpiryReason testReason = new ExpiryReason("0", LocalDateTime.now(), "Testing", "Tester");
        testUser.getDonorDetails().getOrganMap().put(Organs.LIVER, testReason);

        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");

        verifyThat("#updateDeathDetailsOverrideWarningLabel", Node::isVisible);
        verifyThat("#updateDeathDetailsTimeTextField", Node::isDisabled);
        verifyThat("#updateDeathDetailsDatePicker", Node::isDisabled);
        verifyThat("#removeUpdateDeathDetailsButton", Node::isDisabled);
    }

    @Test
    public void testTimeOfDeathCannotBeInFutureHighEdge() {
        LocalTime actualTime = LocalTime.now();
        String inputTime = actualTime.plusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm"));

        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", inputTime);
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");

        verifyThat("#updateDeathDetailsErrorLabel", LabeledMatchers.hasText("The time of death cannot be in the future"));

    }

    @Test
    public void testTimeOfDeathCannotBeInFutureLowEdge() {
        LocalTime actualTime = LocalTime.now();
        String inputTime = actualTime.minusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm"));

        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", inputTime);
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");

        Assert.assertEquals(testUser.getTimeOfDeath().toString(), actualTime.minusMinutes(1).format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    @Test
    public void testTimeOfDeathCannotBeInFutureOnEdge() {
        LocalTime actualTime = LocalTime.now();
        String inputTime = actualTime.format(DateTimeFormatter.ofPattern("HH:mm"));

        loginAsClinician();
        clickOnButton(this, "#updateDeathDetailsButton");
        setDateValue(this, "#updateDeathDetailsDatePicker", LocalDate.now());
        setTextField(this, "#updateDeathDetailsTimeTextField", inputTime);
        clickOnButton(this, "#confirmUpdateDeathDetailsButton");

        Assert.assertEquals(testUser.getTimeOfDeath().toString(), actualTime.format(DateTimeFormatter.ofPattern("HH:mm")));
    }

}
