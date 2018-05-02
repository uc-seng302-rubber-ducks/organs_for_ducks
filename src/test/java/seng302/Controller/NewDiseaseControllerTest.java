package seng302.Controller;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.NodeQuery;
import seng302.App;
import seng302.Model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static seng302.Controller.TableViewsMethod.*;

import static org.testfx.api.FxAssert.verifyThat;

public class NewDiseaseControllerTest extends ApplicationTest {
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

        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.now(), "ABC1244"));
        //Use default clinician
        clickOn("#changeLogin");
        clickOn("#userIDTextField");
        write("0");
        clickOn("#passwordField");
        write("admin");
        clickOn("#loginButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
        clickOn("#addDiseaseButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void createNotChronicDisease(){
        clickOn("#diseaseNameInput");
        write("Dengue Fever");
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("Dengue Fever", getCellValue("#pastDiseaseTableView", 1, 0).toString());
    }
}
