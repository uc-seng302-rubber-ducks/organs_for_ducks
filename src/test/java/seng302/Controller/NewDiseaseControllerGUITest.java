package seng302.Controller;

import static org.junit.Assert.assertEquals;
import static seng302.Utils.TableViewsMethod.getCell;
import static seng302.Utils.TableViewsMethod.getCellValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Model.Disease;
import seng302.Model.User;

public class NewDiseaseControllerGUITest extends ApplicationTest {

    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeClass
    public static void initialization() {
        if (Boolean.getBoolean("headless")) {
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
            System.setProperty("headless.geometry", "1920x1080-32");
        }
    }

    @Before
    public void setUpCreateScene()  throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244"));
        AppController.getInstance().getUsers().get(0).getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));
        AppController.getInstance().getUsers().get(0).getPastDiseases().add(new Disease("B0", false, true, LocalDate.now()));

        //Use default clinician
        clickOn("#changeLogin");
        clickOn("#userIDTextField");
        write("0", 0);
        clickOn("#passwordField");
        write("admin", 0);
        clickOn("#loginButton");
        //verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void createdDiseaseShouldBeInCurrentDiseaseTable(){
        clickOn("#addDiseaseButton");
        clickOn("#diseaseNameInput");
        write("A1", 0);
        //Use default date
        clickOn("#createButton");
        assertEquals("A1", getCellValue("#currentDiseaseTableView", 1, 1).toString());
    }

    @Test
    public void createdCuredDiseaseShouldBeInPastDiseaseTable(){
        clickOn("#addDiseaseButton");
        clickOn("#diseaseNameInput");
        write("A1", 0);
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("A1", getCellValue("#pastDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void updatedDiseaseNameShouldBeDisplayedCorrectly(){
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
    public void updatedDiseaseDateShouldBeDisplayedCorrectly(){
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#diagnosisDateInput");
        for(int i = 0; i < 10; i++) {
            push(KeyCode.RIGHT);
        }
        for(int i = 0; i < 15; i++) {
            push(KeyCode.BACK_SPACE);
        }
        write("12/01/2007", 0);
        clickOn("#createButton");
        assertEquals(LocalDate.parse("2007-01-12", sdf), getCellValue("#currentDiseaseTableView", 0, 0));
    }

    @Test
    public void diseaseShouldMoveToPastDiseaseTableWhenSetToCured(){
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("A0", getCellValue("#pastDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void diseaseShouldMoveToCurrentDiseaseTableWhenNeitherCuredOrChronic() {
        clickOn(getCell("#pastDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#clearSelection");
        clickOn("#createButton");
        assertEquals("B0", getCellValue("#currentDiseaseTableView", 1, 1).toString());
    }

    @Test (expected = NullPointerException.class)
    public void deletedPastDiseaseShouldBeRemovedFromPastDiseases() {
        clickOn(getCell("#pastDiseaseTableView", 0, 0));
        clickOn("#deleteDiseaseButton");
        getCellValue("#pastDiseaseTableView", 0, 0);
    }

    @Test (expected = NullPointerException.class)
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

    @Test (expected = FxRobotException.class)
    public void generalUserShouldNotBeAbleToEditDiseases() {
        clickOn("#userProfileTab");
        //clickOn("#logOutButton");
        clickOn("#backButton");
        clickOn("#detailsTab");
        clickOn("#logoutButton");
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
