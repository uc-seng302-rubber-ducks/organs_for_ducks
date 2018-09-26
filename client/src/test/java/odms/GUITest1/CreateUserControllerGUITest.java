package odms.GUITest1;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import odms.App;
import odms.TestUtils.AppControllerMocker;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.*;
import static org.testfx.api.FxAssert.verifyThat;

public class CreateUserControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
//        CommonTestMethods.runMethods();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        AppController application = AppControllerMocker.getFullMock();

        AppController.setInstance(application);
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class, "--testConfig=true");
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
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this, "#confirmButton");
        verifyThat("#NHIValue", LabeledMatchers.hasText("ADE1987"));
    }

    @Test
    public void testSignUpNoInfo() {
        clickOnButton(this, "#confirmButton");
        verifyThat("#fNameErrorLabel", Node::isVisible);
        verifyThat("#nhiErrorLabel", Node::isVisible);
        verifyThat("#dobErrorLabel", Node::isVisible);
    }

    @Test
    public void testFutureDob() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.now().plusYears(10));
        clickOnButton(this, "#confirmButton");
        verifyThat("#dobErrorLabel", Node::isVisible);
    }

    @Test
    public void testHealthDetails() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOn("#userHealth");
        clickOn("#birthGenderComboBox");
        clickOn("Male");
        setTextField(this, "#heightInput", "1.75");
        setTextField(this, "#weightInput", "65");
        clickOn("#bloodComboBox");
        clickOn("B+");
        clickOn("#alcoholComboBox");
        clickOn("None");
        clickOn("#smokerCheckBox");
        clickOn("#smokerCheckBox");
        clickOnButton(this, "#confirmButton");
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
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setTextField(this, "#preferredFNameTextField", "The Rock");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this, "#confirmButton");
        verifyThat("#pNameValue", LabeledMatchers.hasText("The Rock"));
    }

    @Test
    public void testHomePhoneInput() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setTextField(this, "#phone", "033552847");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pHomePhone", LabeledMatchers.hasText("033552847"));
    }

    @Test
    public void testInvalidHomePhone() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#phone", "asdf");
        clickOnButton(this, "#confirmButton");
        verifyThat("#homePhoneErrorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidEmail() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setTextField(this, "#email", "asdf");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        ;
        clickOnButton(this, "#confirmButton");
        verifyThat("#emailErrorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidMobilePhone() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setTextField(this, "#cell", "asdf");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        clickOnButton(this, "#confirmButton");
        verifyThat("#cellPhoneErrorLabel", Node::isVisible);
    }

    @Test
    public void testValidMobilePhone() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#cell", "0224973642");
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pCellPhone", LabeledMatchers.hasText("0224973642"));
    }

    @Test
    public void testValidEmail() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#email", "dwayneRock@gmail.com");
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pEmail", LabeledMatchers.hasText("dwayneRock@gmail.com"));
    }

    @Test
    public void testValidAddress() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setDateValue(this, "#dobInput", LocalDate.now().minusYears(1));
        clickOn("#userContact");
        setTextField(this, "#streetNumber", "76B");
        setTextField(this, "#street", "Cambridge St");
        setTextField(this, "#neighborhood", "Kirkwood");
        setTextField(this, "#city", "Battlefield");
        setComboBox(this, "#countrySelector", "New Zealand");
        setComboBox(this, "#regionSelector", "Otago");
        setTextField(this, "#zipCode", "8033");
        verifyThat("#regionSelector", Node::isVisible);
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#pAddress", LabeledMatchers.hasText("76B Cambridge St\nKirkwood"));
        verifyThat("#city", LabeledMatchers.hasText("Battlefield"));
        verifyThat("#pRegion", LabeledMatchers.hasText("Otago"));
        verifyThat("#country", LabeledMatchers.hasText("New Zealand"));
        verifyThat("#zipCode", LabeledMatchers.hasText("8033"));
    }

    @Test
    public void testValidEmergencyContact() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#ecName", "John Cena");
        setTextField(this, "#ecCellPhone", "0214583341");
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
        verifyThat("#eCellPhone", LabeledMatchers.hasText("0214583341"));
    }

    @Test
    public void testValidEmergencyContactAddress() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        setDateValue(this, "#dobInput", LocalDate.now().minusYears(1));
        setTextField(this, "#ecName", "John Cena");
        setTextField(this, "#ecCellPhone", "0214583341");
        setTextField(this, "#ecStreetNumber", "55E");
        setTextField(this, "#ecStreet", "Oxford St");
        setTextField(this, "#ecNeighborhood", "Ilam");
        setTextField(this, "#ecCity", "Lichfield");
        setComboBox(this, "#ecRegionSelector", "Chatham Islands");
        setTextField(this, "#ecZipCode", "8035");
        setComboBox(this, "#ecCountrySelector", "New Zealand");
        verifyThat("#ecRegionSelector", Node::isVisible);
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eAddress", LabeledMatchers.hasText("55E Oxford St\nIlam"));
        verifyThat("#ecCity", LabeledMatchers.hasText("Lichfield"));
        verifyThat("#eRegion", LabeledMatchers.hasText("Chatham Islands"));
        verifyThat("#ecCountry", LabeledMatchers.hasText("New Zealand"));
        verifyThat("#ecZipCode", LabeledMatchers.hasText("8035"));
    }

    @Test
    public void testInvalidEmergencyContactName() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#ecCellPhone", "0214583341");
        clickOnButton(this, "#confirmButton");
        verifyThat("#eNameErrorLabel", Node::isVisible);
    }

    @Test
    public void testInvalidEmergencyPhone() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#ecName", "John Cena");
        clickOnButton(this, "#confirmButton");
        verifyThat("#eCellPhoneErrorLabel", Node::isVisible);
    }

    @Test
    public void testAllEmergencyDetails() {
        setTextField(this, "#newUserNhiInput", "ADE1987");
        setTextField(this, "#fNameInput", "Dwayne");
        lookup("#dobInput").queryAs(DatePicker.class).setValue(LocalDate.parse("3/1/2017", DateTimeFormatter.ofPattern("d/M/yyyy")));
        setTextField(this, "#ecName", "John Cena");
        setTextField(this, "#ecCellPhone", "0221557621");
        setTextField(this, "#ecPhone", "033594573");
        setTextField(this, "#ecEmail", "johnCena@gmail.com");
        setTextField(this, "#ecRelationship", "Leader");
        clickOnButton(this, "#confirmButton");
        clickOn("#detailsTab");
        verifyThat("#eName", LabeledMatchers.hasText("John Cena"));
        verifyThat("#eHomePhone", LabeledMatchers.hasText("033594573"));
        verifyThat("#eCellPhone", LabeledMatchers.hasText("0221557621"));
        verifyThat("#eEmail", LabeledMatchers.hasText("johnCena@gmail.com"));
        verifyThat("#relationship", LabeledMatchers.hasText("Leader"));
    }

}
