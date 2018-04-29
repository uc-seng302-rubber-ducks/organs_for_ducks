package seng302.Controller;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Model.User;

public class DeleteUserGUITest extends ApplicationTest{
  @Before
  public void setUpCreateScene() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    clickOn("#userIdTextField");
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
    clickOn("#delete");
  }


}
