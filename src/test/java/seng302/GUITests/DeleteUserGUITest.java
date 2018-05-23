package seng302.GUITests;

import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
@Ignore
public class DeleteUserGUITest extends ApplicationTest{

  @BeforeClass
  public static void initialization() {
    //CommonTestMethods.runHeadless();
  }

  @Before
  public void setUpCreateScene() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIDTextField");
    write("ABC1234");
    clickOn("#loginUButton");
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
    clickOn("#loginUButton");
    verifyThat("#userWarningLabel", LabeledMatchers.hasText("User was not found. \nTo register a new user, please click sign up."));
  }

  @Test
  public void canceledDeletedUser(){
    clickOn("#deleteUser");
    clickOn("Cancel");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }


}
