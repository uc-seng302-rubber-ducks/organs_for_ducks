package seng302.Controller;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.NodeQuery;
import seng302.App;
import seng302.Model.Disease;
import seng302.Model.User;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        AppController.getInstance().getUsers().add(new User("Aa", LocalDate.parse("2000-01-20", dateFormatter), "ABC1244"));
        AppController.getInstance().getUsers().get(0).getCurrentDiseases().add(new Disease("Cancer", true, false, LocalDate.now()));

        //Use default clinician
        clickOn("#changeLogin");
        clickOn("#userIDTextField");
        write("0", 0);
        clickOn("#passwordField");
        write("admin", 0);
        clickOn("#loginButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
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
    public void createNotChronicDisease(){
        clickOn("#addDiseaseButton");
        clickOn("#diseaseNameInput");
        write("Dengue Fever", 0);
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("Dengue Fever", getCellValue("#pastDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void updateDisease1(){
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#diseaseNameInput");
        for(int i = 0; i < 10; i++) {
            push(KeyCode.RIGHT);
        }
        for(int i = 0; i < 30; i++) {
            push(KeyCode.BACK_SPACE);
        }
        write("Love Fever", 0);
        clickOn("#createButton");
        assertEquals("Love Fever", getCellValue("#currentDiseaseTableView", 1, 0).toString());
    }

    @Test
    public void updateDisease2(){
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
        assertEquals("2007-01-12", getCellValue("#currentDiseaseTableView", 0, 0).toString());
    }

    @Test
    public void updateDisease3(){
        clickOn(getCell("#currentDiseaseTableView", 0, 0));
        clickOn("#updateDiseaseButton");
        clickOn("#curedRadioButton");
        clickOn("#createButton");
        assertEquals("Cancer", getCellValue("#pastDiseaseTableView", 1, 0).toString());
    }
}
