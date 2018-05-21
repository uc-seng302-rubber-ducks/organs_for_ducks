package seng302.GUITests;

import javafx.scene.input.KeyCode;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.User;
import seng302.Utils.CommonTestMethods;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class LoginControllerGUITest extends ApplicationTest {

  @BeforeClass
  public static void initialization() {
    CommonTestMethods.runHeadless();
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
    clickOn("#loginUButton");
    verifyThat("#userWarningLabel", LabeledMatchers.hasText("Donor was not found. \nTo register a new donor please click sign up."));
  }

  @Test
  public void validDonorLogin() {
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginUButton");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @Test
  public void ValidClinicianLogin() {
    //Use default clinician
    clickOn("#clinicianTab");
    clickOn("#staffIdTextField");
    write("0");
    clickOn("#staffPasswordField");
    write("admin");
    clickOn("#loginCButton");
    verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
  }

  @Test
  public void clinicianInvalidClinician() {
    clickOn("#clinicianTab");
    clickOn("#staffIdTextField");
    write("-1000");
    clickOn("#staffPasswordField");
    write("admin");
    clickOn("#loginCButton");
    verifyThat("#clinicianWarningLabel", LabeledMatchers.hasText("The Clinician does not exist"));
  }

  @Test
  public void clinicianWrongPassword() {
    clickOn("#clinicianTab");
    clickOn("#staffIdTextField");
    write("0");
    clickOn("#staffPasswordField");
    write("garbledo");
    clickOn("#loginCButton");
    verifyThat("#clinicianWarningLabel", LabeledMatchers.hasText("Your password is incorrect please try again"));

  }

  @Test
  public void validDonorLoginEnterPressed() {
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    press(KeyCode.ENTER);
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @Ignore
  @Test
  public void validAdminLogin() {
    //TODO finish once JSON handler is working with login
    //use default admin
    clickOn("#administratorTab");
    clickOn("#adminUsernameTextField");
    write("default");
    clickOn("#adminPasswordField");
    write("admin");
    clickOn("#loginAButton");
    //verifyThat();
  }

  @Test
  public void invalidAdminLogin() {

    clickOn("#administratorTab");
    clickOn("#adminUsernameTextField");
    write("therock");
    clickOn("#adminPasswordField");
    write("password");
    clickOn("#loginAButton");
    verifyThat("#adminWarningLabel", LabeledMatchers.hasText("The administrator does not exist."));
  }

  @Ignore
  @Test
  public void wrongAdminPassword() {
    //TODO finish once JSON handler is working with login
    clickOn("#administratorTab");
    clickOn("#adminUsernameTextField");
    write("default");
    clickOn("#adminPasswordField");
    write("notpassword");
    clickOn("#loginAButton");
    verifyThat("#adminWarningLabel", LabeledMatchers.hasText("Your password is incorrect. Please try again."));
  }

}
