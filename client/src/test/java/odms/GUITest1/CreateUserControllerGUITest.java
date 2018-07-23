package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import odms.App;
import odms.controller.AppController;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import odms.TestUtils.CommonTestMethods;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;

public class CreateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        AppController application = mock(AppController.class);

        AppController.setInstance(application);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
        clickOn("#signUpButton");
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.setInstance(null);
        FxToolkit.cleanupStages();
    }

    @Test
    public void testOpenSignUpFromLogin() {
        verifyThat("#headerLabel", LabeledMatchers.hasText("Create New User"));
    }

    @Test
    public void testSignUpBasicInfo() {

        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this,"#confirmButton");
        verifyThat("#NHIValue", LabeledMatchers.hasText("ADE1987"));
    }

    @Test
    public void testSignUpNoInfo() {
        clickOnButton(this,"#confirmButton");
        verifyThat("#invalidFirstName", Node::isVisible);
        verifyThat("#invalidNHI", Node::isVisible);
        verifyThat("#invalidDOB", Node::isVisible);
    }

    @Test
    public void testFutureDob() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.now().plusYears(10));
        clickOnButton(this,"#confirmButton");
        verifyThat("#invalidDOB", Node::isVisible);
    }

    @Test
    public void testFutureDOD() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        lookup("#dodInput").queryAs(DatePicker.class).setValue(LocalDate.now().plusYears(10));
        clickOnButton(this,"#confirmButton");
        verifyThat("#invalidDOD", Node::isVisible);
    }

    @Test
    public void testHealthDetails() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOn("#birthGenderComboBox");
        clickOn("Male");
        setTextField(this,"#heightInput","1.75");
        setTextField(this,"#weightInput","65");
        clickOn("#bloodComboBox");
        clickOn("B+");
        clickOn("#alcoholComboBox");
        clickOn("None");
        clickOn("#smokerCheckBox");
        clickOn("#smokerCheckBox");
        clickOnButton(this,"#confirmButton");
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
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        setTextField(this,"#preferredFNameTextField","The Rock");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this,"#confirmButton");
        verifyThat("#pNameValue", LabeledMatchers.hasText("The Rock"));
    }

    @Test
    public void testHomePhoneInput() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        setTextField(this,"#phone","033552847");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("033552847"));
    }

    @Test
    public void testInvalidHomePhone() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#phone","asdf");
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidEmail() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        setTextField(this,"#email","asdf");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));;
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidMobilePhone() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        setTextField(this,"#cell","asdf");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", Node::isVisible);
    }

    @Test
    public void testValidMobilePhone() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#cell","0224973642");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224973642"));
    }

    @Test
    public void testValidEmail() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#email","dwayneRock@gmail.com");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pEmail", LabeledMatchers.hasText("dwayneRock@gmail.com"));
    }

    @Test
    public void testValidAddress() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#email","dwayneRock@gmail.com");
        clickOnButton(this,"#confirmButton");
        Assert.fail("this tests checks nothing so im making it fail JB 20/7/18 - should it be the same as testValidEmail");
    }

    @Test
    public void testValidEmergencyContact() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#ecName","John Cena");
        setTextField(this,"#ecCell","0214583341");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
        verifyThat("#eCellPhone", LabeledMatchers.hasText("0214583341"));
    }

    @Test
    public void testInvalidEmergencyContactName() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#ecCell","0214583341");
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
    }

    @Test
    public void testInvalidEmergencyPhone() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#ecName","John Cena");
        clickOnButton(this,"#confirmButton");
        verifyThat("#errorLabel", LabeledMatchers.hasText("Name and cell phone number are required for an emergency contact."));
    }

    @Test
    public void testAllEmergencyDetails() {
        setTextField(this,"#nhiInput","ADE1987");
        setTextField(this,"#fNameInput","Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this,"#ecName","John Cena");
        setTextField(this,"#ecCell","0221557621");
        setTextField(this,"#ecPhone","033594573");
        setTextField(this,"#ecEmail","johnCena@gmail.com");
        setTextField(this,"#ecRelationship","Leader");
        clickOnButton(this,"#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
        Assert.fail("This needs to check more labels to ensure it works");
    }

}
