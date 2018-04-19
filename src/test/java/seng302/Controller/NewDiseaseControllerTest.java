package seng302.Controller;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.NodeQuery;
import seng302.App;
import static seng302.Controller.TableViewsMethod.*;

import static org.testfx.api.FxAssert.verifyThat;

public class NewDiseaseControllerTest extends ApplicationTest {
    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

//    @Test
//    public void ValidClinicianLogin() {
//        //Use default clinician
//        clickOn("#changeLogin");
//        clickOn("#userIDTextField");
//        write("0");
//        clickOn("#passwordField");
//        write("admin");
//        clickOn("#loginButton");
//        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
//        clickOn("#searchTab");
//        doubleClickOn(cellName("#searchTableView", 0, 0));
//        clickOn("#diseaseTab");
//        clickOn("#addDiseaseButton");
//    }
}
