package seng302.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Then;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.Assert.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Controller.TableViewsMethod.*;

public class ThenSteps {
    @Then("^There are two profiles with first name \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void thereAreTwoProfilesWithFirstNameAndLastName(String arg1, String arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The donor should be stored within the application$")
    public void theDonorShouldBeStoredWithinTheApplication() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^the timestamp should be displayed along with the rest of the profile$")
    public void theTimestampShouldBeDisplayedAlongWithTheRestOfTheProfile() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The profiles for \"([^\"]*)\" and \"([^\"]*)\" are displayed$")
    public void theProfilesForAndAreDisplayed(String arg1, String arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The error message \"([^\"]*)\"$")
    public void theErrorMessage(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^There is an error message$")
    public void thereIsAnErrorMessage() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^The donor should no longer be in the system$")
    public void theDonorShouldNoLongerBeInTheSystem() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my NHI \"([^\"]*)\" along with my other details at the donor view screen")
    public void theIShouldSeeMyNHIAlongWithMyOtherDetailsAtTheDonorViewScreen(String nhi) throws Throwable {
        verifyThat("#NHIValue" , LabeledMatchers.hasText(nhi));
    }

    @Then("^I should see an invalid date of birth, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfBirthErrorMessage(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see an invalid date of death, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfDeathErrorMessage(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my NHI \"([^\"]*)\" first name \"([^\"]*)\", Smoker is marked as \"([^\"]*)\", alcohol \"([^\"]*)\" and date of death \"([^\"]*)\"$")
    public void theIShouldSeeMyNHIFirstNameSmokerIsMarkedAsAlcoholAndDateOfDeath(String arg1, String arg2, String arg3, String arg4, String arg5) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my preferred name \"([^\"]*)\" along with my other details at the donor view screen")
    public void theIShouldSeeMyPreferredNameAlongWithMyOtherDetailsAtTheDonorViewScreen(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see error message \"([^\"]*)\"$")
    public void iShouldSeeErrorMessage(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see my Staff ID \"([^\"]*)\" along with my other details at the clinician view screen$")
    public void theIShouldSeeMyStaffIDAlongWithMyOtherDetailsAtTheClinicianViewScreen(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see the Disease Name \"([^\"]*)\" at the Current Diseases Table$")
    public void iShouldSeeTheDiseaseNameAtTheCurrentDiseasesTable(String diseaseName) throws Throwable {
        assertEquals(diseaseName, getCellValue("#currentDiseaseTableView", 1, 0).toString());
    }

    @Then("^I should see the Disease Name \"([^\"]*)\" and the word \"([^\"]*)\" in red next to disease name at the Current Diseases Table$")
    public void iShouldSeeTheDiseaseNameAndTheWordInRedNextToDiseaseNameAtTheCurrentDiseasesTable(String arg1, String arg2) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("^I should see the Disease Name \"([^\"]*)\" at the Past Diseases Table$")
    public void iShouldSeeTheDiseaseNameAtThePastDiseasesTable(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
