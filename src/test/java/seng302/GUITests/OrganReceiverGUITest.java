package seng302.GUITests;



import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static seng302.Utils.TableViewsMethod.getCell;
import static seng302.Utils.ListViewsMethod.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Stack;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Controller.UserController;
import seng302.Model.*;
import seng302.Utils.ListViewsMethod;

public class OrganReceiverGUITest extends ApplicationTest{

    private DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private AppController mockAppController = mock(AppController.class);
    private UserController mockUserController = mock(UserController.class);
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
    public void setUpCreateScene()  throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        AppController.getInstance().getUsers().add(testUser);

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
        assertEquals("Kidney", getRowValue("#currentlyReceivingListView", 0).toString());
    }

    @Test
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
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        assertEquals("Kidney", getRowValue("#notReceivingListView", 0).toString());

    }

    @Test
    public void reasonsAndDatesShouldBeCorrectlyStored() {
        clickOn("#organsComboBox");
        clickOn("Kidney");
        clickOn("#registerButton");
        getListView("#currentlyReceivingListView").getSelectionModel().select(0);
        clickOn("#deRegisterButton");
        clickOn("#registerationErrorRadioButton");
        clickOn("#okButton");
        getListView("#notReceivingListView").getSelectionModel().select(0);
        clickOn("#reRegisterButton");
        Stack<ReasonAndDateHolderForReceiverDetails> testStack = testUser.getReceiverDetails().getOrgans().get(Organs.KIDNEY);
        assertEquals(LocalDate.now(), testStack.peek().getStartDate());
        System.out.println(testStack.peek().toString());
        assertEquals(OrganDeregisterReason.REGISTRATION_ERROR, testStack.peek().getOrganDeregisterReason());
        testStack.pop();
        assertEquals(LocalDate.now(), testStack.peek().getStartDate());

    }

}
