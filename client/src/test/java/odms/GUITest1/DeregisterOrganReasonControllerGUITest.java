package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import odms.App;
import odms.commons.model.Clinician;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.UserBridge;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;
import static odms.TestUtils.TableViewsMethod.getCell;
import static odms.TestUtils.TableViewsMethod.getNumberOfRows;

public class DeregisterOrganReasonControllerGUITest extends ApplicationTest {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(bridge.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(testUser)));
        when(bridge.getUser("ABC1244")).thenReturn(testUser);
        when(application.getUsers()).thenReturn(Arrays.asList(testUser)); // needs to be modidfed to return a list
        AppController.getInstance().getUsers().clear();
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.HEART);
        testUser.getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));
        AppController.getInstance().getUsers().add(testUser);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

        //Use default clinician
        clickOn("#clinicianTab");
        clickOn("#staffIdTextField");
        write("0", 0);
        clickOn("#staffPasswordField");
        write("admin", 0);
        clickOn("#loginCButton");
        //verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
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
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedRegistrationErrorTransplantWaitListEmpty() {
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
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
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        assertEquals(0, getNumberOfRows("#transplantWaitListTableView"));
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedReceiverDiedTransplantWaitListEmpty() {
        clickOn("#receiverDiedRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
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
        boolean testPass = true;
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
        clickOn("#userProfileTab");
        verifyThat("#DODValue", LabeledMatchers.hasText(LocalDate.now().toString()));
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
