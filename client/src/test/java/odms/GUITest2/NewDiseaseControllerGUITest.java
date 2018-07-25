package odms.GUITest2;

import javafx.scene.input.KeyCode;
import odms.App;
import odms.commons.model.Clinician;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.utils.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static odms.TestUtils.TableViewsMethod.getCell;
import static odms.TestUtils.TableViewsMethod.getCellValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NewDiseaseControllerGUITest extends ApplicationTest {

    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private AppController controller;
    private UserBridge bridge;
    private ClinicianBridge clinicianBridge;
    private LoginBridge loginBridge;
    private AdministratorBridge administratorBridge;
    private TransplantBridge transplantBridge;
    private User testUser;

    @BeforeClass
    public static void initialization() {
        //CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        controller = mock(AppController.class);
        bridge = mock(UserBridge.class);
        clinicianBridge = mock(ClinicianBridge.class);
        loginBridge = mock(LoginBridge.class);
        administratorBridge = mock(AdministratorBridge.class);
        transplantBridge = mock(TransplantBridge.class);

        Clinician clinician = new Clinician();
        clinician.setStaffId("0");

        AppController.setInstance(controller);
        when(controller.getUserBridge()).thenReturn(bridge);
        when(controller.getClinicianBridge()).thenReturn(clinicianBridge);
        when(controller.getAdministratorBridge()).thenReturn(administratorBridge);
        when(controller.getLoginBridge()).thenReturn(loginBridge);

        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(controller.getTransplantBridge()).thenReturn(transplantBridge);

        when(transplantBridge.getWaitingList(anyInt(), anyInt(), anyString(), anyString(), any(Collection.class))).thenReturn(new ArrayList());

        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");
        testUser.getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));
        testUser.getPastDiseases().add(new Disease("B0", false, true, LocalDate.now()));

        when(bridge.getUsers(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(UserOverview.fromUser(testUser)));
        when(bridge.getUser(anyString())).thenReturn(testUser);

        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);

        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this,"#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "admin");
        clickOnButton(this, "#loginCButton");
        //verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void createdDiseaseShouldBeInCurrentDiseaseTable() {
        clickOnButton(this,"#addDiseaseButton");
        setTextField(this,"#diseaseNameInput", "A1");
        //Use default date
        clickOnButton(this,"#createButton");
        assertEquals("A1", getCellValue("#currentDiseaseTableView", 1, 1).toString());

    }

    @Test
    public void createdCuredDiseaseShouldBeInPastDiseaseTable() { //FAIL
        clickOnButton(this,"#addDiseaseButton");
        setTextField(this,"#diseaseNameInput","A1");
        clickOn("#curedRadioButton");
        clickOnButton(this,"#createButton");
        assertEquals("A1", getCellValue("#pastDiseaseTableView", 1, 1).toString());
    }

    @Test
    public void updatedDiseaseNameShouldBeDisplayedCorrectly() {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#diseaseNameInput");
//        for(int i = 0; i < 10; i++) {
//            push(KeyCode.RIGHT);
//        }
        push(KeyCode.RIGHT);
        push(KeyCode.RIGHT);

//        for(int i = 0; i < 30; i++) {
//            push(KeyCode.BACK_SPACE);
//        }
        push(KeyCode.BACK_SPACE);
        push(KeyCode.BACK_SPACE);

        write("A1", 0);
        clickOn("#createButton");
        assertEquals("A1", getCellValue("#currentDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void updatedDiseaseDateShouldBeDisplayedCorrectly() {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#diagnosisDateInput");
        for (int i = 0; i < 10; i++) {
            push(KeyCode.RIGHT);
        }
        for (int i = 0; i < 15; i++) {
            push(KeyCode.BACK_SPACE);
        }
        write("12/01/2007", 0);
        clickOn("#createButton");
        assertEquals(LocalDate.parse("2007-01-12", sdf), ((LocalDate) (getCellValue("#currentDiseaseTableView", 0, 0))));
    }

    @Test
    public void diseaseShouldMoveToPastDiseaseTableWhenSetToCured() { //FAIL
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("A0", getCellValue("#pastDiseaseTableView", 1, 1).toString());
    }

    @Test
    public void diseaseShouldMoveToCurrentDiseaseTableWhenNeitherCuredOrChronic() {
        clickOn(getCell("#pastDiseaseTableView", 0, 0));
        clickOnButton(this,"#updateDiseaseButton");
        clickOnButton(this,"#clearSelection");
        clickOnButton(this,"#createButton");
        assertEquals("B0", getCellValue("#currentDiseaseTableView", 1, 1).toString());
    }

    @Test(expected = NullPointerException.class)
    public void deletedPastDiseaseShouldBeRemovedFromPastDiseases() {
        clickOn(getCell("#pastDiseaseTableView", 0, 0));
        clickOn("#deleteDiseaseButton");
        getCellValue("#pastDiseaseTableView", 0, 0);
    }

    @Test(expected = NullPointerException.class)
    public void deletedCurrentDiseaseShouldBeRemovedFromCurrentDisease() throws NullPointerException {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#deleteDiseaseButton");
        getCellValue("#currentDiseaseTableView", 0, 0);
    }

    //Only other things I can think of testing are the ordering
    @Test
    public void deletedChronicDiseaseShouldNotBeDeletedFromCurrentDiseases() {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#chronicRadioButton");
        clickOn("#createButton");
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#deleteDiseaseButton");
        assertEquals("A0", getCellValue("#currentDiseaseTableView", 1, 0).toString());

    }

    @Test(expected = FxRobotException.class)
    public void generalUserShouldNotBeAbleToEditDiseases() {
        clickOn("#userProfileTab");
        //clickOn("#logOutButton");
        clickOn("#backButton");
        clickOn("#detailsTab");
        clickOn("#logoutButton");
        clickOn("#userIDTextField");
        write("ABC1244", 0);
        clickOn("#loginButton");
        clickOn("#diseaseTab");
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        //These three should fail
        clickOn("#updateDiseaseButton");
        clickOn("#deleteDiseaseButton");
        clickOn("#addDiseaseButton");
    }

}
