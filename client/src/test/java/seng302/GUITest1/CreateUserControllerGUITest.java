package seng302.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.App;
import seng302.Utils.CommonTestMethods;
import odms.controller.AppController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.testfx.api.FxAssert.verifyThat;

public class CreateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        clickOn("#signUpButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void testOpenSignUpFromLogin() {
        verifyThat("#headerLabel", LabeledMatchers.hasText("Create New User"));
    }

    @Test
    public void testSignUpBasicInfo() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
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
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.now().plusYears(10));
        clickOn("#confirmButton");
        verifyThat("#invalidDOB", Node::isVisible);
    }

    @Test
    public void testFutureDOD() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#dodInput").queryAs(DatePicker.class).setValue(LocalDate.now().plusYears(10));
        clickOn("#confirmButton");
        verifyThat("#invalidDOD", Node::isVisible);
    }

    @Test
    public void testHealthDetails() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOn("#birthGenderComboBox");
        clickOn("Male");
        lookup("#heightInput").queryAs(TextField.class).setText("1.75");
        lookup("#weightInput").queryAs(TextField.class).setText("65");
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
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#preferredFNameTextField").queryAs(TextField.class).setText("The Rock");
        clickOn("#confirmButton");
        verifyThat("#pNameValue", LabeledMatchers.hasText("The Rock"));
    }

    @Test
    public void testHomePhoneInput() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#phone").queryAs(TextField.class).setText("033552847");
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("033552847"));
    }

    @Test
    public void testInvalidHomePhone() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#phone").queryAs(TextField.class).setText("asdf");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidEmail() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#email").queryAs(TextField.class).setText("asdf");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidMobilePhone() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#cell").queryAs(TextField.class).setText("asdf");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testValidMobilePhone() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#cell").queryAs(TextField.class).setText("0224973642");
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224973642"));
    }

    @Test
    public void testValidEmail() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#email").queryAs(TextField.class).setText("dwayneRock@gmail.com");
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pEmail", LabeledMatchers.hasText("dwayneRock@gmail.com"));
    }

    @Test
    public void testValidAddress() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#email").queryAs(TextField.class).setText("dwayneRock@gmail.com");
        clickOn("#confirmButton");
    }

    @Test
    public void testValidEmergencyContact() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#ecName").queryAs(TextField.class).setText("John Cena");
        lookup("#ecCell").queryAs(TextField.class).setText("0214583341");
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
        verifyThat("#eCellPhone", LabeledMatchers.hasText("0214583341"));
    }

    @Test
    public void testInvalidEmergencyContactName() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#ecCell").queryAs(TextField.class).setText("0214583341");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
    }

    @Test
    public void testInvalidEmergencyPhone() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#ecName").queryAs(TextField.class).setText("John Cena");
        clickOn("#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
    }

    @Test
    public void testAllEmergencyDetails() {
        lookup("#nhiInput").queryAs(TextField.class).setText("ADE1987");
        lookup("#fNameInput").queryAs(TextField.class).setText("Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#ecName").queryAs(TextField.class).setText("John Cena");
        lookup("#ecPhone").queryAs(TextField.class).setText("033594573");
        lookup("#ecCell").queryAs(TextField.class).setText("0221557621");
        //lookup("#ecRegion").queryAs(TextField.class).setText("Canterbury");
        lookup("#ecEmail").queryAs(TextField.class).setText("johnCena@gmail.com");
        lookup("#ecRelationship").queryAs(TextField.class).setText("Leader");
        clickOn("#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
    }

}
