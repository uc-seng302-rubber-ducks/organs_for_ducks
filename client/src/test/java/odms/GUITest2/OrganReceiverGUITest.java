package odms.GUITest2;


import javafx.collections.FXCollections;
import javafx.scene.control.TableView;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.exception.UnauthorisedException;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

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
import static odms.TestUtils.TableViewsMethod.getCellValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class OrganReceiverGUITest extends ApplicationTest {

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");
    private Collection<UserOverview> overviews = Collections.singletonList(UserOverview.fromUser(testUser));
    private OrgansBridge organsBridge = mock(OrgansBridge.class);

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException, UnauthorisedException {

        UserBridge bridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        AppController application = AppControllerMocker.getFullMock();
        TransplantBridge transplantBridge = mock(TransplantBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        AppController.setInstance(application);
        when(application.getUserBridge()).thenReturn(bridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getToken()).thenReturn("ahaahahahahhaha");
        when(application.getOrgansBridge()).thenReturn(organsBridge);

        when(application.getTransplantList()).thenReturn(new ArrayList<>());
        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        doNothing().when(organsBridge).getAvailableOrgansList(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), anyString(), any(), eq(null));
        doNothing().when(application).addUserOverview(any(UserOverview.class));

        when(application.getUserOverviews()).thenReturn(new HashSet<>(overviews));
        when(bridge.getUser("ABC1244")).thenReturn(testUser);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");

        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);

        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField" ,"0");
        setTextField(this, "#staffPasswordField","admin");

        clickOnButton(this,"#loginCButton");
        //verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        interact(() -> {
            lookup("#searchTableView").queryAs(TableView.class).setItems(FXCollections.observableList(Collections.singletonList(UserOverview.fromUser(testUser))));
            lookup("#searchTableView").queryAs(TableView.class).refresh();
        });
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
        setComboBox(this,"#organsComboBox",Organs.KIDNEY);
        clickOnButton(this,"#registerButton");
        assertEquals("Kidney", getCellValue("#currentlyWaitingFor", 0,0).toString());
    }

    @Test
    public void organShouldMoveCorrectlyBetweenTablesWhenMoveButtonsClicked() {
        //Setup
        setComboBox(this,"#organsComboBox", Organs.KIDNEY);
        clickOnButton(this,"#registerButton");
        getCellValue("#currentlyWaitingFor",0,0);

        //Test reRegister does nothing when already in currentlyReceiving
        clickOnButton(this, "#reRegisterButton");
        assertEquals("Kidney", getCellValue("#currentlyWaitingFor", 0,0).toString());

        //Test deRegister successfully moves organ to notReceiving
//        clickOn(getCell("#currentlywaitingFor",0,0 ));

        interact(() -> lookup("#currentlyWaitingFor").queryAs(TableView.class).getSelectionModel().select(0));
        clickOn( "#deRegisterButton");
        clickOn("#registrationErrorRadioButton");
        clickOn("#okButton");
        assertEquals("Kidney", getCellValue("#noLongerWaitingForOrgan", 0,0).toString());

    }
}
