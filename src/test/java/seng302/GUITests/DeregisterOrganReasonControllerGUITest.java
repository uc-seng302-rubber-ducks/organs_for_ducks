package seng302.GUITests;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.Disease;
import seng302.Model.Organs;
import seng302.Model.User;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static seng302.Utils.TableViewsMethod.getCell;
import static seng302.Utils.TableViewsMethod.getCellValue;

public class DeregisterOrganReasonControllerGUITest extends ApplicationTest {

    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Before
    public void setUpCreateScene()  throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244"));
        AppController.getInstance().getUsers().get(0).getReceiverDetails().startWaitingForOrgan(Organs.HEART);
        AppController.getInstance().getUsers().get(0).getCurrentDiseases().add(new Disease("A0", false, false, LocalDate.now()));

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
        clickOn("#receiverTab");
        clickOn("Heart");
        clickOn("#deRegisterButton");
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedTransplantWaitListEmpty (){
        boolean testPass = false;
        clickOn("#transplantReceivedRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        try{
            getCellValue("#transplantWaitListTableView", 0, 0);
        } catch (NullPointerException e) {
            testPass = true;
        }
        assertTrue(testPass);
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedRegistrationErrorTransplantWaitListEmpty (){
        boolean testPass = false;
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        try{
            getCellValue("#transplantWaitListTableView", 0, 0);
        } catch (NullPointerException e) {
            testPass = true;
        }
        assertTrue(testPass);
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedDiseaseCuredTransplantWaitListEmpty (){
        boolean testPass = false;
        clickOn("#diseaseCuredRadioButton");
        clickOn("#diseaseNameComboBox");
        clickOn("A0");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        try{
            getCellValue("#transplantWaitListTableView", 0, 0);
        } catch (NullPointerException e) {
            testPass = true;
        }
        assertTrue(testPass);
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedReceiverDiedTransplantWaitListEmpty (){
        boolean testPass = false;
        clickOn("#receiverDiedRadioButton");
        clickOn("#okButton");
        clickOn("#userProfileTab");
        clickOn("#backButton");
        clickOn("#transplantWaitListTab");
        try{
            getCellValue("#transplantWaitListTableView", 0, 0);
        } catch (NullPointerException e) {
            testPass = true;
        }
        assertTrue(testPass);
    }


}
