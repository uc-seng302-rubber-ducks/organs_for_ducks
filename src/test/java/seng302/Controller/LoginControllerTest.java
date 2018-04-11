package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.User;

public class LoginControllerTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    new App().start(stage);
  }

  @Test
  public void cannotLogin() {
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("Donor was not found.\nTo register a new donor please click sign up."));
  }

  @Test
  public void canLogin() {
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginButton");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @Test
  public void clinicianLogin() {

  }

  @Test
  public void clinicianInvalid() {

  }
}
