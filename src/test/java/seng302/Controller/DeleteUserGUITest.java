package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.User;

public class DeleteUserGUITest extends ApplicationTest{
  @Before
  public void setUpCreateScene() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginButton");
  }

  @After
  public void tearDown() throws TimeoutException {
    AppController.getInstance().getUsers().clear();
    FxToolkit.cleanupStages();
  }

  @Test
  public void deletedUser(){
    clickOn("#deleteUser");
    clickOn("OK");
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginButton");
    verifyThat("#warningLabel", LabeledMatchers.hasText("Donor was not found. \nTo register a new donor please click sign up."));
  }

  @Test
  public void canceledDeletedUser(){
    clickOn("#deleteUser");
    clickOn("Cancel");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }


}
