package seng302.steps;

import cucumber.api.java.en.Then;
import javafx.scene.Node;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.Utils.TableViewsMethod;
import seng302.model.User;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Utils.TableViewsMethod.getCellValue;

public class ThenSteps extends ApplicationTest {
    @Then("^There are two profiles with first name \"([^\"]*)\" and last name \"([^\"]*)\"$")
    public void thereAreTwoProfilesWithFirstNameAndLastName(String name, String arg2) {
        ArrayList<User> user = CucumberTestModel.getController().findUsers(name);
        assertTrue(user.size() == 2);
    }

    @Then("^The user should be stored within the application$")
    public void theUserShouldBeStoredWithinTheApplication() {
        assertTrue(
                CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()) != null);
    }

    @Then("^The user should no longer be in the system$")
    public void theUserShouldNoLongerBeInTheSystem() {
        assertTrue(
                CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()) == null);
    }

    @Then("^I should see my NHI \"([^\"]*)\" along with my other details at the user view screen")
    public void theIShouldSeeMyNHIAlongWithMyOtherDetailsAtTheUserViewScreen(String nhi) {
        verifyThat("#NHIValue", LabeledMatchers.hasText(nhi));
    }

    @Then("^I should see an invalid date of birth, \"([^\"]*)\" error message$")
    public void iShouldSeeAnInvalidDateOfBirthErrorMessage(String arg1) {
        verifyThat("#invalidDOB", Node::isVisible);
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
            System.out.println(errorMessage);
            errorMessage = errorMessage.replaceAll("\\\\n", "\n");
            System.out.println(errorMessage);
            verifyThat("#userWarningLabel", LabeledMatchers.hasText(errorMessage));
        }
    }
}
