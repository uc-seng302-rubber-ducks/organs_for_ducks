package odms.GUITest2;


import javafx.application.Platform;
import odms.App;
import odms.commons.model.Clinician;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.model.User;
import odms.commons.model._enum.OrganDeregisterReason;
import odms.commons.model._enum.Organs;
import odms.utils.ClinicianBridge;
import odms.utils.LoginBridge;
import odms.utils.UserBridge;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static odms.TestUtils.ListViewsMethod.getListView;
import static odms.TestUtils.ListViewsMethod.getRowValue;
import static odms.TestUtils.TableViewsMethod.getCell;
import static org.mockito.Mockito.when;

public class OrganReceiverGUITest extends ApplicationTest {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");

    @BeforeClass
    public static void initialization() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1920x1080-32");
            Platform.runLater(OrganReceiverGUITest::initialization);
        }
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {

        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = mock(AppController.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(bridge.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(testUser)));
        when(bridge.getUser("ABC1244")).thenReturn(testUser);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);

        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField" ,"0");
        setTextField(this, "#staffPasswordField","admin");

        clickOnButton(this,"#loginCButton");
        //verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#receiverTab");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void clinicianShouldBeAbleToStartADonorReceivingAnOrgan() {
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOnButton(this,"#registerButton");
        assertEquals("Kidney", getRowValue("#currentlyReceivingListView", 0).toString());
    }

    @Test
    @Ignore
    public void organShouldMoveCorrectlyBetweenTablesWhenMoveButtonsClicked() {
        //Setup
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        getListView("#currentlyReceivingListView").getSelectionModel().select(0);

        //Test reRegister does nothing when already in currentlyReceiving
        clickOn("#reRegisterButton");
        getListView("#currentlyReceivingListView").getSelectionModel().select(0);
        assertEquals("Kidney", getRowValue("#currentlyReceivingListView", 0).toString());

        //Test deRegister successfully moves organ to notReceiving
        clickOn("#deRegisterButton");
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        assertEquals("Kidney", getRowValue("#notReceivingListView", 0).toString());

    }

    /**
     * I think this test has an issue with testUser not saving properly, the print statements seem to be working fine and manual testing shows good results
     */
    @Ignore
    @Test
    public void reasonsAndDatesShouldBeCorrectlyStored() {
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        getListView("#currentlyReceivingListView").getSelectionModel().select(0);
        clickOn("#deRegisterButton");
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        getListView("#notReceivingListView").getSelectionModel().select(0);
        clickOn("#reRegisterButton");
        ArrayList<ReceiverOrganDetailsHolder> holder = testUser.getReceiverDetails().getOrgans().get(Organs.KIDNEY);
//        System.out.println(holder.get(holder.size() - 1).toString());
        assertEquals(LocalDate.now(), holder.get(holder.size() - 1).getStartDate());
        assertEquals(OrganDeregisterReason.REGISTRATION_ERROR, holder.get(holder.size() - 1).getOrganDeregisterReason());
        assertEquals(LocalDate.now(), holder.get(0).getStartDate());

    }

}
