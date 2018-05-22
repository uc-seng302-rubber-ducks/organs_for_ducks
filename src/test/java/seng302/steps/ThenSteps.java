package seng302.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Utils.TableViewsMethod.getCellValue;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import org.testfx.matcher.control.LabeledMatchers;

public class ThenSteps {
    @Then("^There are two profiles with first name \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void thereAreTwoProfilesWithFirstNameAndLastName(String arg1, String arg2) {

    }

    @Then("^The user should be stored within the application$")
    public void theUserShouldBeStoredWithinTheApplication() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the timestamp should be displayed along with the rest of the profile$")
    public void theTimestampShouldBeDisplayedAlongWithTheRestOfTheProfile() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The profiles for \"([^\"]*)\" and \"([^\"]*)\" are displayed$")
    public void theProfilesForAndAreDisplayed(String arg1, String arg2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The error message \"([^\"]*)\"$")
    public void theErrorMessage(String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^There is an error message$")
    public void thereIsAnErrorMessage() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The user should no longer be in the system$")
    public void theUserShouldNoLongerBeInTheSystem() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my NHI \"([^\"]*)\" along with my other details at the user view screen")
    public void theIShouldSeeMyNHIAlongWithMyOtherDetailsAtTheUserViewScreen(String nhi) {
        verifyThat("#NHIValue" , LabeledMatchers.hasText(nhi));
    }

    @Then("^I should see an invalid date of birth, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfBirthErrorMessage(String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see an invalid date of death, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfDeathErrorMessage(String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my NHI \"([^\"]*)\" first name \"([^\"]*)\", Smoker is marked as \"([^\"]*)\", alcohol \"([^\"]*)\" and date of death \"([^\"]*)\"$")
    public void theIShouldSeeMyNHIFirstNameSmokerIsMarkedAsAlcoholAndDateOfDeath(String arg1,
        String arg2, String arg3, String arg4, String arg5) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my preferred name \"([^\"]*)\" along with my other details at the user view screen")
    public void theIShouldSeeMyPreferredNameAlongWithMyOtherDetailsAtTheUserViewScreen(
        String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see error message for disease name \"([^\"]*)\"$")
    public void iShouldSeeErrorMessageForDiseaseName(String errorMessage) {
        verifyThat("#diseaseNameInputErrorMessage", LabeledMatchers.hasText(errorMessage));
    }

    @Then("^I should see error message for diagnosis date \"([^\"]*)\"$")
    public void iShouldSeeErrorMessageForDiagnosisDate(String errorMessage) {
        verifyThat("#diagnosisDateInputErrorMessage", LabeledMatchers.hasText(errorMessage));
    }

    @Then("^I should see my Staff ID \"([^\"]*)\" along with my other details at the clinician view screen$")
    public void theIShouldSeeMyStaffIDAlongWithMyOtherDetailsAtTheClinicianViewScreen(String arg1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see the Disease Name \"([^\"]*)\" at the Current Diseases Table$")
    public void iShouldSeeTheDiseaseNameAtTheCurrentDiseasesTable(String diseaseName) {
        assertEquals(diseaseName, getCellValue("#currentDiseaseTableView", 1, 0).toString());
    }

    @Then("^I should see the Disease Name \"([^\"]*)\" and the word chronic in red next to disease name at the Current Diseases Table$")
    public void iShouldSeeTheDiseaseNameAndTheWordInRedNextToDiseaseNameAtTheCurrentDiseasesTable(
        String diseaseName) {
        assertEquals(diseaseName, getCellValue("#currentDiseaseTableView", 1, 0).toString());
        assertTrue((boolean) getCellValue("#currentDiseaseTableView", 2, 0));

    }

    @Then("^the donor should not be contained within the transplant waiting list$")
    public void theDonorShouldNotBeContainedWithinTheTransplantWaitingList() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see error message \"([^\"]*)\"$")
    public void iShouldSeeErrorMessage(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
