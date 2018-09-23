package odms.steps;

import cucumber.api.java.en.Then;
import javafx.scene.Node;
import odms.TestUtils.TableViewsMethod;
import odms.commons.model.User;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.List;

import static odms.TestUtils.TableViewsMethod.getCellValue;
import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

public class ThenSteps extends ApplicationTest {
    @Then("^There are two profiles with first name \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void thereAreTwoProfilesWithFirstNameAndLastName(String name, String arg2) {
        List<User> user = CucumberTestModel.getController().findUsers(name);
        assertEquals(2, user.size());
    }

    @Then("^The user should be stored within the application$")
    public void theUserShouldBeStoredWithinTheApplication() {
        assertTrue(
                CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()) != null);
    }

    @Then("^The user should no longer be in the system$")
    public void theUserShouldNoLongerBeInTheSystem() {
        assertTrue(
                CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).isDeleted());
    }

    @Then("^I should see my NHI \"([^\"]*)\" along with my other details at the user view screen")
    public void theIShouldSeeMyNHIAlongWithMyOtherDetailsAtTheUserViewScreen(String nhi) {
        verifyThat("#NHIValue", LabeledMatchers.hasText(nhi));
    }

    @Then("^I should see an invalid date of birth, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfBirthErrorMessage(String arg1) {
        verifyThat("#dobErrorLabel", Node::isVisible);
    }

    @Then("^I should see an invalid date of death, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfDeathErrorMessage(String arg1) {
        verifyThat("#invalidDOD", Node::isVisible);
    }

    @Then("^I should see my NHI \"([^\"]*)\" first name \"([^\"]*)\", Smoker is marked as \"([^\"]*)\", alcohol \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void theIShouldSeeMyNHIFirstNameSmokerIsMarkedAsAlcoholAndDateOfDeath(String nhi,
                                                                                 String fName, String smoker, String alcohol, String dod) {
        verifyThat("#NHIValue", LabeledMatchers.hasText(nhi));
        verifyThat("#fNameValue", LabeledMatchers.hasText(fName));
        verifyThat("#smokerValue", LabeledMatchers.hasText(smoker));
        verifyThat("#alcoholValue", LabeledMatchers.hasText(alcohol));
        verifyThat("#DOBValue", LabeledMatchers.hasText(dod));
    }

    @Then("^I should see my preferred name \"([^\"]*)\" along with my other details at the user view screen")
    public void theIShouldSeeMyPreferredNameAlongWithMyOtherDetailsAtTheUserViewScreen(
            String pName) {
        verifyThat("#pNameValue", LabeledMatchers.hasText(pName));
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
    public void theIShouldSeeMyStaffIDAlongWithMyOtherDetailsAtTheClinicianViewScreen(
            String staffId) {
        verifyThat("#staffIdLabel", LabeledMatchers.hasText(staffId));
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

    @Then("^the user should not be contained within the transplant waiting list$")
    public void theDonorShouldNotBeContainedWithinTheTransplantWaitingList() {
        assertTrue(TableViewsMethod.getNumberOfRows("#transplantWaitListTableView") == 0);
    }

    @Then("^I should see error message \"([^\"]*)\"$")
    public void iShouldSeeErrorMessage(String errorMessage) {
        if (CucumberTestModel.isClinicianLogin()) {
            verifyThat("#clinicianWarningLabel", LabeledMatchers.hasText(errorMessage));
        } else {
            errorMessage = errorMessage.replaceAll("\\\\n", "\n");
            verifyThat("#userWarningLabel", LabeledMatchers.hasText(errorMessage));
        }
    }

    @Then("^an entry for \"([^\"]*)\" and \"([^\"]*)\" should be in the cache$")
    public void an_entry_for_and_should_be_in_the_cache(String drugA, String drugB) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        String key = drugA + "-" + drugB;
        assertTrue(CucumberTestModel.getMedicationInteractionCache().containsKey(key));
    }

    @Then("^the cache should not contain an entry with key \"([^\"]*)\"$")
    public void the_cache_should_not_contain_an_entry_with_key(String key) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        assertFalse(CucumberTestModel.getMedicationInteractionCache().containsKey(key));
    }


    @Then("^the cache should be empty$")
    public void the_cache_should_be_empty() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        assertTrue(CucumberTestModel.getMedicationInteractionCache().isEmpty());
    }
}
