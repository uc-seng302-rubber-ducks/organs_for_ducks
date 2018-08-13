package odms.GUITest2;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.TestUtils.CommonTestMethods;
import odms.bridge.*;
import odms.commons.model.Clinician;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static odms.TestUtils.TableViewsMethod.getCell;
import static odms.TestUtils.TableViewsMethod.getCellValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;

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
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException, IOException {
        controller = AppControllerMocker.getFullMock();
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
        when(controller.getToken()).thenReturn("EZ");

        when(loginBridge.loginToServer(anyString(),anyString(), anyString())).thenReturn("lsdjfksd");
        when(clinicianBridge.getClinician(anyString(), anyString())).thenReturn(clinician);
        when(controller.getTransplantBridge()).thenReturn(transplantBridge);

        when(controller.getTransplantList()).thenReturn(new ArrayList<>());

        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244");
        testUser.getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));
        testUser.getPastDiseases().add(new Disease("B0", false, true, LocalDate.now()));

        when(controller.getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(testUser)));
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
        interact(() -> {
            lookup("#searchTableView").queryAs(TableView.class).setItems(FXCollections.observableList(Collections.singletonList(UserOverview.fromUser(testUser))));
            lookup("#searchTableView").queryAs(TableView.class).refresh();
        });
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    @Ignore
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
        clickOnButton(this,"#updateDiseaseButton");
        setTextField(this, "#diseaseNameInput","A1");
        clickOnButton(this,"#createButton");
        assertEquals("A1", getCellValue("#currentDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void updatedDiseaseDateShouldBeDisplayedCorrectly() {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOnButton(this, "#updateDiseaseButton");
        setDateValue(this, "#diagnosisDateInput",LocalDate.of(2007, 1, 12));
        clickOnButton(this, "#createButton");
        assertEquals(LocalDate.of(2007, 1, 12), getCellValue("#currentDiseaseTableView", 0, 0));
    }

    @Test
    public void diseaseShouldMoveToPastDiseaseTableWhenSetToCured() { //FAIL
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOnButton(this,"#updateDiseaseButton");
        clickOn("#curedRadioButton");
        clickOnButton(this,"#createButton");
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
        clickOnButton(this,"#deleteDiseaseButton");
        getCellValue("#pastDiseaseTableView", 0, 0);
    }

    @Test(expected = NullPointerException.class)
    public void deletedCurrentDiseaseShouldBeRemovedFromCurrentDisease() throws NullPointerException {
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOnButton(this,"#deleteDiseaseButton");
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
        clickOn("OK");
        assertEquals("A0", getCellValue("#currentDiseaseTableView", 1, 0).toString());

    }

    @Test @Ignore
    public void generalUserShouldNotBeAbleToEditDiseases() {
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
        clickOn("#fileMenuClinician");
        clickOn("#logoutMenuClinician");
        clickOn("#userIDTextField");
        write("ABC1244", 0);
        clickOn("#loginButton");
        clickOn("#diseaseTab");
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        //These three should fail
        assertFalse(lookup("#createDiseaseButton").queryAs(Button.class).isVisible());
        verifyThat("#deleteDiseaseButton", Node::isVisible);
        verifyThat("#addDiseaseButton", Node::isVisible);
    }

}
