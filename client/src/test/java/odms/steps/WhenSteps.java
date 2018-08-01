package odms.steps;

import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import odms.App;
import odms.TestUtils.TableViewsMethod;
import odms.commands.CreateUser;
import odms.commands.DeleteUser;
import odms.commands.View;
import odms.commons.model.UserBuilder;
import odms.commons.model.dto.UserOverview;
import odms.view.CLI;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import picocli.CommandLine;
import picocli.CommandLine.RunLast;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;


public class WhenSteps extends ApplicationTest {

    @When("^I view the previously created user")
    public void iViewThePreviouslyCreatedUser() {
        String[] args = {"-NHI=" + CucumberTestModel.getUserNhi()};
        View command = new View();
        new CommandLine(command).parseWithHandler(new RunLast(), System.err, args);
    }

    @When("^I use the view command to view all users")
    public void iUseTheViewCommandToViewAllUsers() {
        String[] args = {"-a"};
        View command = new View();
        new CommandLine(command).parseWithHandler(new RunLast(), System.err, args);
    }

    @When("^I delete the user with the above NHI$")
    public void iDeleteTheUserWithTheAboveNHI() {
        String[] args = {CucumberTestModel.getUserNhi()};
        DeleteUser command = new DeleteUser();
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", CucumberTestModel.getController());
    }

    @When("^I register a user with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterAUserWithTheNHIFirstNameLastNameAndDateOfBirth(String nhi, String fName,
                                                                        String lName, String dob) throws IOException {
        CucumberTestModel.setUserNhi(nhi);
        String[] args = {fName, lName, nhi, dob};
        CreateUser command = new CreateUser();
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        when(CucumberTestModel.getUserBridge().getUser(anyString())).thenReturn(new UserBuilder().setNhi(nhi).setFirstName(fName).setLastName(lName).setDateOfBirth(LocalDate.parse(dob, DateTimeFormatter.ISO_LOCAL_DATE)).build());
    }

    @When("^I register a user using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterAUserUsingTheGUIWithTheNHIFirstNameAndDateOfBirth(String nhi, String fName,
                                                                           String dob) {
        setTextField(this, "#nhiInput", nhi);
        setTextField(this, "#fNameInput", fName);
        clickOn("#dobInput");
        write(dob);
        clickOn("#nhiInput");
        CucumberTestModel.setUserNhi(nhi);
        CucumberTestModel.setUser(new UserBuilder().setNhi(nhi).setFirstName(fName).setDateOfBirth(LocalDate.parse(dob, DateTimeFormatter.ofPattern("D/M/yyyy"))).build());
    }

    @When("^Clicked on the Create Profile button$")
    public void clickedOnTheCreateProfileButton() throws IOException {
        when(CucumberTestModel.getUserBridge().getUser(anyString())).thenReturn(CucumberTestModel.getUser());
        clickOnButton(this, "#confirmButton");
    }

    @When("^I register a user using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\", date of birth \"([^\"]*)\" and date of death \"([^\"]*)\"$")
    public void iRegisterAUserUsingTheGUIWithTheNHIFirstNameDateOfBirthAndDateOfDeath(String nhi,
                                                                                      String fName, String dob, String dod) {
        setTextField(this, "#nhiInput", nhi);
        setTextField(this, "#fNameInput", fName);
        clickOn("#dobInput");
        write(dob);
        clickOn("#dodInput");
        write(dod);
        clickOn("#nhiInput");
        CucumberTestModel.setUserNhi(nhi);
    }

    @When("^entered preferred name \"([^\"]*)\"$")
    public void enteredPreferredName(String pName) {
        setTextField(this, "#preferredFNameTextField", pName);
        CucumberTestModel.getUser().setPreferredFirstName(pName);
    }

    @When("^I entered NHI \"([^\"]*)\"$")
    public void iEnteredNHI(String nhi) {
        (lookup("#userIDTextField")).queryAs(TextField.class).setText(nhi);
    }

    @When("^clicked on user Login button$")
    public void clickedOnUserLoginButton() {
        CucumberTestModel.setIsClinicianLogin(false);
        clickOnButton(this,"#loginUButton");
    }

    @When("^clicked on clinician Login button$")
    public void clickedOnClinicianLoginButton() throws IOException {
        when(CucumberTestModel.getController().getUserOverviews()).thenReturn(Collections.singleton(UserOverview.fromUser(CucumberTestModel.getUser())));
        CucumberTestModel.setIsClinicianLogin(true);
        clickOnButton(this, "#loginCButton");
    }

    @When("^clicked on Create Button$")
    public void clickedOnCreateButton() throws IOException {
        when(CucumberTestModel.getUserBridge().getUser(anyString())).thenReturn(CucumberTestModel.getUser());
        clickOn("#createButton");
    }

    @When("^I clicked on Login As Clinician Button$")
    public void iClickedOnLoginAsClinicianButton() {
        clickOn("#clinicianTab");
    }

    @When("^I entered Staff ID \"([^\"]*)\" and Password \"([^\"]*)\"$")
    public void iEnteredStaffIDAndPassword(String staffId, String password) {
        setTextField(this, "#staffIdTextField", staffId);
        setTextField(this, "#staffPasswordField", password);
    }

    @When("^I entered Disease Name \"([^\"]*)\" and used the default Diagnosis Date$")
    public void iEnteredDiseaseNameAndUsedTheDefaultDiagnosisDate(String diseaseName) {
        clickOn("#diseaseNameInput");
        write(diseaseName);
    }

    @When("^clicked on Status Chronic$")
    public void clickedOnStatusChronic() {
        clickOn("#chronicRadioButton");
    }


    @When("^I entered Disease Name \"([^\"]*)\" and Diagnosis Date \"([^\"]*)\"$")
    public void iEnteredDiseaseNameAndDiagnosisDate(String diseaseName, String diagnosisDate) {
        clickOn("#diseaseNameInput");
        write(diseaseName);
        clickOn("#diagnosisDateInput");
        for (int i = 0; i < 20; i++) { //arbitrarily long number to ensure all is deleted
            push(KeyCode.BACK_SPACE);
        }
        write(diagnosisDate);
        clickOn("#diseaseNameInput");
    }

    @When("^The user is updated to have died on \"([^\"]*)\"$")
    public void theUserIsUpdatedToHaveDiedOn(String dod) {
        clickOn("#editMenuUser");
        clickOn("#editDetailsUser");
        clickOn("#dodInput");
        write(dod);
        clickOnButton(this, "#confirmButton");
    }

    @And("^I open the user page$")
    public void iOpenTheUserPage() throws IOException {
        clickOn("#searchTab");
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
    }

    @And("^with health info, which consist of birth gender \"([^\"]*)\", height \"([^\"]*)\", weight \"([^\"]*)\", blood type \"([^\"]*)\", alcohol consumption \"([^\"]*)\", and unticked on the smoker checkbox$")
    public void withHealthInfoWhichConsistOfBirthGenderHeightWeightBloodTypeAlcoholConsumptionAndUntickedOnTheSmokerCheckbox(
            String bGender, String height, String weight, String bloodType, String alcCons) {
        clickOn("#birthGenderComboBox");
        clickOn(bGender);
        setTextField(this, "#heightInput", height);
        clickOn("#weightInput");
        write(weight);
        clickOn("#bloodComboBox");
        clickOn(bloodType);
        clickOn("#alcoholComboBox");
        clickOn(alcCons);
    }

    @When("^I go back to the clinician screen$")
    public void iGoBackToTheClinicianScreen() {
        clickOn("#userProfileTab");
        clickOn("#fileMenuUser");
        clickOn("#logoutUser");
    }

    @And("^I open the waiting list tab$")
    public void iOpenTheWaitingListTab() {
        clickOn("#transplantWaitListTab");
    }

    @When("^the app is closed and reopened$")
    public void the_app_is_closed_and_reopened() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        clickOn("#userIDTextField");
        write("ABC1234");
        clickedOnUserLoginButton();
    }

    @When("^the cache is cleared$")
    public void the_cache_is_cleared() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        CucumberTestModel.getMedicationInteractionCache().removeOlderThan(LocalDateTime.now());
    }

    @When("^all data before \"([^\"]*)\" is removed$")
    public void all_data_before_is_removed(String date) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        CucumberTestModel.getMedicationInteractionCache().removeOlderThan(LocalDateTime.now().minusDays(2));
    }

    @When("^the interactions between \"([^\"]*)\" and \"([^\"]*)\" are requested$")
    public void the_interactions_between_and_are_requested(String drugA, String drugB) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        CucumberTestModel.getHttpRequester().getDrugInteractions(drugA, drugB);
    }
}
