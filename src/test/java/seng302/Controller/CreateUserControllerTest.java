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

  @Test
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
    verifyThat("#bloodTypeValue", LabeledMatchers.hasText("B+"));
    verifyThat("#heightValue", LabeledMatchers.hasText("1.75"));
    verifyThat("#weightValue", LabeledMatchers.hasText("65.0"));
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

  @Test
  public void testHomePhoneInput() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#phoneInput");
    write("3552847");
    clickOn("#confirmButton");
    clickOn("#detailsTab");
    verifyThat("#pHomePhone", LabeledMatchers.hasText("3552847"));
  }

  @Test @Ignore
  public void testInvalidHomePhone() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#phoneInput");
    write("asdf");
    clickOn("#confirmButton");
    //TODO: Check that an invalid label is shown
  }

  @Test
  public void testInvalidEmail() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#emailInput");
    write("asdf");
    clickOn("#confirmButton");
    //TODO: Check that an invalid label is shown
  }

  @Test
  public void testInvalidMobilePhone() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#cellInput");
    write("asdf");
    clickOn("#confirmButton");
    //TODO: Check that an invalid label is shown
  }

  @Test
  public void testValidMobilePhone() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#cellInput");
    write("0224973642");
    clickOn("#confirmButton");
    clickOn("#detailsTab");
    verifyThat("#pCellPhone", LabeledMatchers.hasText("0224973642"));
  }

  @Test
  public void testValidEmail() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#emailInput");
    write("dwayneRock@gmail.com");
    clickOn("#confirmButton");
    clickOn("#detailsTab");
    verifyThat("#pEmail", LabeledMatchers.hasText("dwayneRock@gmail.com"));
  }

  @Test
  public void testValidAddress() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#emailInput");
    write("dwayneRock@gmail.com");
    clickOn("#confirmButton");
  }

  @Test
  public void testValidEmergencyContact() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#ecNameInput");
    write("John Cena");
    clickOn("#ecCellInput");
    write("0214583341");
    clickOn("#confirmButton");
    clickOn("#detailsTab");
    verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
    verifyThat("#eCellPhone", LabeledMatchers.hasText("0214583341"));
  }

  @Test
  public void testInvalidEmergencyContactName() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#ecCellInput");
    write("0214583341");
    clickOn("#confirmButton");
    verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
  }

  @Test
  public void testInvalidEmergencyPhone() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#ecNameInput");
    write("John Cena");
    clickOn("#confirmButton");
    verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
  }

  @Test
  public void testAllEmergencyDetails() {
    clickOn("#nhiInput");
    write("ADE1987");
    clickOn("#fNameInput");
    write("Dwayne");
    clickOn("#dobInput");
    write("3/1/2017");
    clickOn("#ecNameInput");
    write("John Cena");
    clickOn("#ecPhoneInput");
    write("3594573");
    clickOn("#ecCellInput");
    write("0221557621");
    clickOn("#ecAddressInput");
    write("123 Example St");
    clickOn("#ecRegionInput");
    write("Canterbury");
    clickOn("#ecEmailInput");
    write("johnCena@gmail.com");
    clickOn("#ecRelationshipInput");
    write("Leader");
    clickOn("#confirmButton");
    clickOn("#detailsTab");
    verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
  }

}
