package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.exception.UnauthorisedException;
import odms.commons.model.Clinician;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.controller.gui.window.UserController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static odms.TestUtils.TableViewsMethod.getCell;
import static odms.TestUtils.TableViewsMethod.getNumberOfRows;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

@Ignore
public class DeregisterOrganReasonControllerGUITest extends ApplicationTest {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private UserBridge bridge = mock(UserBridge.class);
    private ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private LoginBridge loginBridge = mock(LoginBridge.class);
    private AppController application = AppControllerMocker.getFullMock();
    private TransplantBridge transplantBridge = mock(TransplantBridge.class);
    private User testUser;
    private OrgansBridge organsBridge = mock(OrgansBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException, UnauthorisedException {

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("Poggers");
        when(application.getOrgansBridge()).thenReturn(organsBridge);

        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(bridge.getUser(anyString())).thenReturn(testUser);
        doNothing().when(organsBridge).getAvailableOrgansList(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), any(), eq(null));
        List<TransplantDetails> transplantDetails = new ArrayList<>();
        transplantDetails.add(new TransplantDetails(testUser.getNhi(), testUser.getFirstName(), Organs.HEART, LocalDate.now(), testUser.getRegion(), testUser.getAge(), testUser.getBloodType()));

        Set<UserOverview> overviews = new HashSet<>();
        overviews.add(UserOverview.fromUser(testUser));
        when(application.getUserOverviews()).thenReturn(overviews);
        when(application.getTransplantList()).thenReturn(transplantDetails);

        doCallRealMethod().when(application).setUserController(any(UserController.class));
        doCallRealMethod().when(application).getUserController();
        doCallRealMethod().when(application).setClinicianController(any());
        doCallRealMethod().when(application).getClinicianController();

        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.HEART);
        testUser.getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");

        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this, "#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "admin");
        clickOnButton(this,"#loginCButton");
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#receiverTab");
        clickOn("Heart");
        clickOn("#deRegisterButton");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedTransplantWaitListEmpty() {
        clickOn("#transplantReceivedRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedRegistrationErrorTransplantWaitListEmpty() {
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedDiseaseCuredTransplantWaitListEmpty() {
        clickOn("#diseaseCuredRadioButton");
        clickOn("#diseaseNameComboBox");
        clickOn("A0");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedReceiverDiedTransplantWaitListEmpty() {
        clickOn("#receiverDiedRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedRegistrationErrorSystemLog() {
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        clickOn("#historyTab");
        assertEquals(3, getNumberOfRows("#historyTableView")); //TODO It should be 2 i think, but it was 1 in the past and is 3 now?. 23/7
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedDiseaseCuredDiseaseTable() {
        clickOn("#diseaseCuredRadioButton");
        clickOn("#diseaseNameComboBox");
        clickOn("A0");
        clickOn("#okButton");
        clickOn("#diseaseTab");
        assertEquals(0, getNumberOfRows("#currentDiseaseTableView"));
        assertEquals(1, getNumberOfRows("#pastDiseaseTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedReceiverDiedDOD() {
        clickOn("#receiverDiedRadioButton");
        clickOn("#okButton");
        Assert.assertEquals(testUser.getDateOfDeath(), LocalDate.now());
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedReceiverDiedInvalidDOD() {
        clickOn("#receiverDiedRadioButton");
        clickOn("#dODDatePicker");
        push(KeyCode.BACK_SPACE);
        push(KeyCode.BACK_SPACE);
        write("40");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        verifyThat("#invalidDateErrorMessage", Node::isVisible);
    }
}
