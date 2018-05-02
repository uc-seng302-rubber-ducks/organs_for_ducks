package seng302.Controller;



import static org.testfx.api.FxAssert.verifyThat;

import java.time.LocalDate;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Model.User;

import java.time.LocalDate;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;
@Ignore
public class DeleteClinicianUserGUITest extends ApplicationTest{
  @Before
  public void setUpCreateScene() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    AppController.getInstance().getUsers().add(new User("A", LocalDate.now(), "ABC1234"));
    AppController.getInstance().getUsers().add(new User("Aa", LocalDate.now(), "ABC1244"));
    clickOn("#changeLogin");
    clickOn("#userIDTextField");
    write("0");
    clickOn("#passwordField");
    write("admin");
    clickOn("#loginButton");
    clickOn("#searchTab");
    doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
  }

  @After
  public void tearDown() throws TimeoutException {
    AppController.getInstance().getUsers().clear();
    FxToolkit.cleanupStages();
  }

  @Test
  public void deleteUser(){
    clickOn("#deleteUser");
    clickOn("OK");
    verifyThat("#fNameLabel", LabeledMatchers.hasText("Default"));

  }

  @Test
  public void canceledDeleteUser(){
    clickOn("#deleteUser");
    clickOn("Cancel");
    clickOn("#backButton");
    verifyThat("#fNameLabel", LabeledMatchers.hasText("Default"));

  }

}
