package seng302.Controller;

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
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Utils.TableViewsMethod;

import static org.junit.Assert.assertTrue;
import static seng302.Utils.TableViewsMethod.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static seng302.Utils.TableViewsMethod.getCell;
import static seng302.Utils.TableViewsMethod.getCellValue;

public class DeregisterOrganReasonControllerGUITest extends ApplicationTest {

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

    @Test
    public void deregisterOrganReasonTransplantReceivedRegistrationErrorSystemLog (){
        boolean testPass = true;
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        clickOn("#historyTab");
        try{
            assertEquals("Initial registering of the organ Heart was an error for receiver Aa", getCellValue("#historyTableView", 1, 0));
        } catch (NullPointerException e) {
            testPass = false;
        }
        assertTrue(testPass);
    }

    @Test
    public void deregisterOrganReasonTransplantReceivedDiseaseCuredDiseaseTable (){
        boolean testPass = true;
        clickOn("#diseaseCuredRadioButton");
        clickOn("#diseaseNameComboBox");
        clickOn("A0");
        clickOn("#okButton");
        clickOn("#diseaseTab");
        try{
            assertEquals("A0", getCellValue("#pastDiseaseTableView", 1, 0));
        } catch (NullPointerException e) {
            testPass = false;
        }
        assertTrue(testPass);
    }
}
