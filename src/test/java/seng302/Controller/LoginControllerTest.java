package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.api.FxToolkit.registerPrimaryStage;

import java.time.LocalDate;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.User;

public class LoginControllerTest extends ApplicationTest {
//
//  @BeforeClass
//  public static void setupSpec() throws Exception {
//    if (Boolean.getBoolean("headless")) {
//      System.setProperty("testfx.robot", "glass");
//      System.setProperty("testfx.headless", "true");
//      System.setProperty("prism.order", "sw");
//      System.setProperty("prism.text", "t2k");
//      System.setProperty("java.awt.headless", "true");
//    }
//    registerPrimaryStage();
//  }

  @Override
  public void start(Stage stage) throws Exception {
    new App().start(stage);
  }

  @Test
  public void invalidDonorLogin() {
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("Donor was not found.\nTo register a new donor please click sign up."));
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

  }
}
