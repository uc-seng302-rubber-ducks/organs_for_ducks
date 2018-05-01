package seng302.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.When;
import org.testfx.framework.junit.ApplicationTest;

public class WhenSteps extends ApplicationTest {
        @When("^I view the previously created donor$")
    public void iViewThePreviouslyCreatedDonor() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I use the view command to view all donors$")
    public void iUseTheViewCommandToViewAllDonors() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I delete the donor with the above NHI$")
    public void iDeleteTheDonorWithTheAboveNHI() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I register a donor with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterADonorWithTheNHIFirstNameLastNameAndDateOfBirth(String arg1, String arg2, String arg3, String arg4) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I register a donor using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void iRegisterADonorUsingTheGUIWithTheNHIFirstNameAndDateOfBirth(String nhi, String fName, String dob) throws Throwable {
        clickOn("#nhiInput");
        write(nhi);
        clickOn("#fNameInput");
        write(fName);
        clickOn("#dobInput");
        write(dob);
    }

    @When("^Clicked on the Create Profile button$")
    public void clickedOnTheCreateProfileButton() throws Throwable {
        clickOn("#confirmButton");
    }

    @When("^I register a donor using the GUI with the NHI \"([^\"]*)\", first name \"([^\"]*)\", date of birth \"([^\"]*)\" and date of death \"([^\"]*)\"$")
    public void iRegisterADonorUsingTheGUIWithTheNHIFirstNameDateOfBirthAndDateOfDeath(String nhi, String fName, String dob, String dod) throws Throwable {
        clickOn("#nhiInput");
        write(nhi);
        clickOn("#fNameInput");
        write(fName);
        clickOn("#dobInput");
        write(dob);
        clickOn("#dodInput");
        write(dod);
    }

    @When("^with health info, which consist of birth gender \"([^\"]*)\", height (\\d+)\\.(\\d+), weight (\\d+), blood type \"([^\"]*)\", alcohol consumption \"([^\"]*)\", and unticked on the smoker checkbox$")
    public void with_health_info_which_consist_of_birth_gender_height_weight_blood_type_alcohol_consumption_and_unticked_on_the_smoker_checkbox(String arg1, int arg2, int arg3, int arg4, String arg5, String arg6) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^entered preferred name \"([^\"]*)\"$")
    public void enteredPreferredName(String pName) throws Throwable {
        clickOn("#preferredFNameTextField");
        write(pName);
    }

    @When("^I entered NHI \"([^\"]*)\"$")
    public void iEnteredNHI(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^clicked on Login button$")
    public void clickedOnLoginButton() throws Throwable {
        clickOn("#loginButton");
    }

    @When("^clicked on Create Button$")
    public void clickedOnCreateButton() throws Throwable{
        clickOn("#createButton");
    }

    @When("^I clicked on Login As Clinician Button$")
    public void iClickedOnLoginAsClinicianButton() throws Throwable {
        clickOn("#changeLogin");
    }

    @When("^I entered Staff ID \"([^\"]*)\" and Password \"([^\"]*)\"$")
    public void iEnteredStaffIDAndPassword(String staffId, String password) throws Throwable {
        clickOn("#userIDTextField");
        write(staffId);
        clickOn("#passwordField");
        write(password);
    }

    @When("^I entered Disease Name \"([^\"]*)\" and used the default Diagnosis Date$")
    public void iEnteredDiseaseNameAndUsedTheDefaultDiagnosisDate(String diseaseName) throws Throwable {
        clickOn("#diseaseNameInput");
        write(diseaseName);
    }

    @When("^clicked on Status Chronic$")
    public void clickedOnStatusChronic() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^clicked on Status Cured$")
    public void clickedOnStatusCured() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("^I entered Disease Name \"([^\"]*)\" and Diagnosis Date \"([^\"]*)\"$")
    public void iEnteredDiseaseNameAndDiagnosisDate(String arg1, String arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

}
