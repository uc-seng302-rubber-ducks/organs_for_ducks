package seng302.steps;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import java.util.Scanner;
import javafx.scene.control.TextField;
import org.testfx.framework.junit.ApplicationTest;
import picocli.CommandLine;
import seng302.Controller.CliCommands.CreateUser;
import seng302.Controller.CliCommands.DeleteUser;
import seng302.Utils.TableViewsMethod;

public class WhenSteps extends ApplicationTest {

    @When("^I view the previously created user")
    public void iViewThePreviouslyCreatedUser() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I use the view command to view all users")
    public void iUseTheViewCommandToViewAllUsers() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I delete the user with the above NHI$")
    public void iDeleteTheUserWithTheAboveNHI() {
        Scanner mockScn = mock(Scanner.class);
        when(mockScn.next()).thenReturn("y");
        String[] args = {CucumberTestModel.getUserNhi()};
        DeleteUser command = new DeleteUser();
        command.setController(CucumberTestModel.getController());
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    }

    @When("^I register a user with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterAUserWithTheNHIFirstNameLastNameAndDateOfBirth(String nhi, String fName,
        String lName, String dob) {
        CucumberTestModel.setUserNhi(nhi);
        String[] args = {fName, lName, nhi, dob};
        CreateUser command = new CreateUser();
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    }

    @When("^I register a user using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterAUserUsingTheGUIWithTheNHIFirstNameAndDateOfBirth(String nhi, String fName,
        String dob) {
        clickOn("#nhiInput");
        write(nhi);
        clickOn("#fNameInput");
        write(fName);
        clickOn("#dobInput");
        write(dob);
        CucumberTestModel.setUserNhi(nhi);
    }

    @When("^Clicked on the Create Profile button$")
    public void clickedOnTheCreateProfileButton() {
        clickOn("#confirmButton");
    }

    @When("^I register a user using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\", date of birth \"([^\"]*)\" and date of death \"([^\"]*)\"$")
    public void iRegisterAUserUsingTheGUIWithTheNHIFirstNameDateOfBirthAndDateOfDeath(String nhi,
        String fName, String dob, String dod) {
        clickOn("#nhiInput");
        write(nhi);
        clickOn("#fNameInput");
        write(fName);
        clickOn("#dobInput");
        write(dob);
        clickOn("#dodInput");
        write(dod);
        CucumberTestModel.setUserNhi(nhi);
    }

    @When("^with health info, which consist of birth gender \"([^\"]*)\", height ((\\d+)\\.(\\d+)), weight (\\d+), blood type \"([^\"]*)\", alcohol consumption \"([^\"]*)\", and unticked on the smoker checkbox$")
    public void with_health_info_which_consist_of_birth_gender_height_weight_blood_type_alcohol_consumption_and_unticked_on_the_smoker_checkbox(
        String arg1, int arg2, int arg3, int arg4, String arg5, String arg6) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^entered preferred name \"([^\"]*)\"$")
    public void enteredPreferredName(String pName) {
        clickOn("#preferredFNameTextField");
        write(pName);
    }

    @When("^I entered NHI \"([^\"]*)\"$")
    public void iEnteredNHI(String nhi) {
        clickOn("#userIDTextField");
        (lookup("#userIDTextField")).queryAs(TextField.class).setText(nhi);
    }

    @When("^clicked on user Login button$")
    public void clickedOnUserLoginButton() {
        clickOn("#loginUButton");
    }

    @When("^clicked on clinician Login button$")
    public void clickedOnClinicianLoginButton() {
        clickOn("#loginCButton");
    }

    @When("^clicked on Create Button$")
    public void clickedOnCreateButton() {
        clickOn("#createButton");
    }

    @When("^I clicked on Login As Clinician Button$")
    public void iClickedOnLoginAsClinicianButton() {
        clickOn("#clinicianTab");
    }

    @When("^I entered Staff ID \"([^\"]*)\" and Password \"([^\"]*)\"$")
    public void iEnteredStaffIDAndPassword(String staffId, String password) {
        clickOn("#staffIdTextField");
        write(staffId);
        clickOn("#staffPasswordField");
        write(password);
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
        doubleClickOn("#diagnosisDateInput");
        write(diagnosisDate);
    }

    @When("^The user is updated to have died on \"([^\"]*)\"$")
    public void theUserIsUpdatedToHaveDiedOn(String dod) {
        clickOn("#editDetailsButton");
        clickOn("#dodInput");
        write(dod);
        clickOn("#confirmButton");
    }

    @And("^I open the user page$")
    public void iOpenTheUserPage() {
        clickOn("#searchTab");
        doubleClickOn(TableViewsMethod.getCell("#searchTableView", 0, 0));
    }
}
