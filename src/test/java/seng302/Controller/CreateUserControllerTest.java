package seng302.Controller;

import static org.testfx.api.FxAssert.verifyThat;

import java.util.concurrent.TimeoutException;
import javafx.scene.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;

public class CreateUserControllerTest extends ApplicationTest {

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

//  @Before
//  public void setup() throws Exception {
//    ApplicationTest.launch(App.class);
//  }

  @Before
  public void setUpCreateScene() throws TimeoutException {
    FxToolkit.registerPrimaryStage();
    FxToolkit.setupApplication(App.class);
    AppController.getInstance().getUsers().clear();
    clickOn("#signUpButton");
  }

  @After
  public void tearDown() {
    AppController.getInstance().getUsers().clear();
  }

  @Test
  public void testOpenSignUpFromLogin() {
    verifyThat("#headerLabel", LabeledMatchers.hasText("Create New User"));
  }

  @Test
  public void testSignUpBasicInfo() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#confirmButton");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ADE1987"));
  }

  @Test
  public void testSignUpNoInfo() {
    clickOn("#confirmButton");
    verifyThat("#invalidFirstName", Node::isVisible);
    verifyThat("#invalidNHI", Node::isVisible);
    verifyThat("#invalidDOB", Node::isVisible);
  }

  @Test @Ignore
  public void testFutureDob() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2500");
    clickOn("#confirmButton");
    verifyThat("#invalidDOB", Node::isVisible);
  }

  @Test
  public void testFutureDOD() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#dodInput");
    write("2/5/2500");
    clickOn("#confirmButton");
    verifyThat("#invalidDOD", Node::isVisible);
  }

  @Test
  public void testHealthDetails() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#birthGenderComboBox");
    clickOn("Male");
    clickOn("#heightInput");
    write("1.75");
    clickOn("#weightInput");
    write("65");
    clickOn("#bloodComboBox");
    clickOn("B+");
    clickOn("#alcoholComboBox");
    clickOn("None");
    clickOn("#smokerCheckBox");
    clickOn("#smokerCheckBox");
    clickOn("#confirmButton");
    verifyThat("#NHIValue", LabeledMatchers.hasText("ADE1987"));
    verifyThat("#fNameValue", LabeledMatchers.hasText("Dwayne"));
    verifyThat("#smokerValue", LabeledMatchers.hasText("No"));
    verifyThat("#alcoholValue", LabeledMatchers.hasText("None"));
    verifyThat("#DOBValue", LabeledMatchers.hasText("2017-01-03"));
  }

  @Test
  public void testPreferredName() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#preferredFNameTextField");
    write("The Rock");
    clickOn("#confirmButton");
    verifyThat("#pNameValue", LabeledMatchers.hasText("The Rock"));
  }

}
