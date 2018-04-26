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
import seng302.Model.EmergencyContact;
import seng302.Model.User;

public class ClinicianFilterTest extends ApplicationTest {

  @Before
  public void setUp() throws TimeoutException{
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    User adam = new User("ABC1234", LocalDate.now(), null, "Male", "Female", 1.75, 78, "A+", "None",true,
    "2 Sherbet Drive", "Fairyland", "033567721", "02044436727", "sherberto@gmail.com",
        new EmergencyContact("Letifa", "0118999124"), "Adam John Fairie", "Adam", "Abathur - Destroyer of worlds", "John",
        "Fairie");
    AppController.getInstance().getUsers().add(adam);
    clickOn("#changeLogin");
    clickOn("#userIDTextField");
    write("0");
    clickOn("#passwordField");
    write("admin");
    clickOn("#loginButton");
    clickOn("#searchTab");
  }

  @Test
  public void testFilterName() {
    clickOn("#searchTextField");
    write("Adam");
    doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
    verifyThat("#NHIValue", LabeledMatchers.hasText("ABC1234"));
  }

  @After
  public void tearDown() {
    AppController.getInstance().getUsers().clear();
  }
}
