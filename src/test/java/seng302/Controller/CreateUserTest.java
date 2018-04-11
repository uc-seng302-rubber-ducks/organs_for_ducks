package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;

public class CreateUserTest extends ApplicationTest {

  @Override
  public void start(Stage stage) throws Exception {
    new App().start(stage);
  }

  @Test
  public void testOpenSignUpFromLogin() {
    clickOn("#signUpButton");
    verifyThat("#headerLabel", LabeledMatchers.hasText("Create New User"));
  }

}
