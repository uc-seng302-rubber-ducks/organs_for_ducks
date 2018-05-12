package seng302.Controller;



import static org.junit.Assert.assertEquals;
import static seng302.Utils.TableViewsMethod.getCell;
import static seng302.Utils.TableViewsMethod.getCellValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import cucumber.api.java.cs.A;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.input.KeyCode;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobotException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.Disease;
import seng302.Model.User;

import static seng302.Utils.TableViewsMethod.getCell;

public class OrganReceiverTest extends ApplicationTest{

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

        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1244"));

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
    }


    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void clinicianShouldBeAbleToStartADonorReceivingAnOrgan(){
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().select(0);
        assertEquals("Kidney", AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().getSelectedItem().toString());
    }

    @Test
    public void organShouldMoveCorrectlyBetweenTablesWhenMoveButtonsClicked() {
        //Setup
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().select(0);

        //Test reRegister does nothing when already in currentlyReceiving
        clickOn("#reRegisterButton");
        AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().select(0);
        assertEquals("Kidney", AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().getSelectedItem().toString());
        //Test deRegister successfully moves organ to notReceiving
        clickOn("#deRegisterButton");
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        AppController.getInstance().getDonorController().notReceivingListView.getSelectionModel().select(0);
        assertEquals("Kidney", AppController.getInstance().getDonorController().notReceivingListView.getSelectionModel().getSelectedItem());

    }

    @Test
    public void doubleClickingAnOrganShouldCorrectlyDisplayStartAndStopDates() {
        //Setup
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().select(0);
        clickOn("#deRegisterButton");
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        AppController.getInstance().getDonorController().notReceivingListView.getSelectionModel().select(0);
        clickOn("#reRegisterButton");
        AppController.getInstance().getDonorController().currentlyReceivingListView.getSelectionModel().select(0);
        AppController.getInstance().getDonorController().openOrganFromDoubleClick(AppController.getInstance().getDonorController().notReceivingListView);
        AppController.getInstance().getDonorController().organDate.organTimeTable.getSelectionModel().select(0);
        assertEquals("LocalDate", AppController.getInstance().getDonorController().organDate.organTimeTable.getSelectionModel().getSelectedItem().toString());
    }

}
