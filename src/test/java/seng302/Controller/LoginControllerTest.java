package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextFlowMatchers;
import org.testfx.matcher.control.TextMatchers;
import org.testfx.util.NodeQueryUtils;
import seng302.App;
import seng302.Model.User;

public class LoginControllerTest extends ApplicationTest {

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
  public void startUp() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
  }

  @After
  public void tearDown() throws TimeoutException {
    AppController.getInstance().getUsers().clear();
    FxToolkit.cleanupStages();
  }

  @Test
  public void invalidDonorLogin() {
    clickOn("#userIDTextField");
    write("AD");
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("Donor was not found. \nTo register a new donor please click sign up."));
  }

  @Test
  public void validDonorLogin() {
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginButton");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @Test
  public void ValidClinicianLogin() {
    //Use default clinician
    clickOn("#changeLogin");
    clickOn("#userIDTextField");
    write("0");
    clickOn("#passwordField");
    write("admin");
    clickOn("#loginButton");
    verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
  }

  @Test
  public void clinicianInvalidClinician() {
    clickOn("#changeLogin");
    clickOn("#userIDTextField");
    write("-1000");
    clickOn("#passwordField");
    write("admin");
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("The Clinician does not exist"));
  }

  @Test
  public void clinicianWrongPassword() {
    clickOn("#changeLogin");
    clickOn("#userIDTextField");
    write("0");
    clickOn("#passwordField");
    write("garbledo");
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("Your password is incorrect please try again"));

  }

  @Test
  public void validDonorLoginEnterPressed() {
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    press(KeyCode.ENTER);
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @Test
  public void testChangeLoginButtonChanges() {
    clickOn("#changeLogin");
    clickOn("#changeLogin");
    clickOn("#changeLogin");
    clickOn("#changeLogin");
    clickOn("#changeLogin");
    clickOn("#changeLogin");
    verifyThat("#idLabel", LabeledMatchers.hasText("NHI:"));
  }


}
