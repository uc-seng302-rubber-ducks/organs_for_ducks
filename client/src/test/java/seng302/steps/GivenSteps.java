package seng302.steps;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import odms.App;
import odms.controller.AppController;
import odms.model.User;
import odms.model._enum.Organs;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Utils.TableViewsMethod.getCell;


public class GivenSteps extends ApplicationTest {

    AppController controller = AppController.getInstance();
    String userNhi = null;

    @After
    public void tearDown() throws TimeoutException {
        if (FxToolkit.isFXApplicationThreadRunning()) {
            FxToolkit.cleanupStages();
        }
    }

    @Given("^I have started the CLI$")
    public void iHaveStartedTheCLI() {
        controller = mock(AppController.class);
    }

    @Given("^I have started the GUI$")
    public void iHaveStartedTheGUI() throws Throwable {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
    }

    @Given("^a user with the NHI \"([^\"]*)\" exists$")
    public void aUserWithTheNHIExists(String NHI) {
        CucumberTestModel.setUserNhi(NHI);
        if (CucumberTestModel.getController().findUser(NHI) == null) {
            CucumberTestModel.getController().getUsers().add(new User("A", LocalDate.now().minusYears(20), NHI));
        }
        assertTrue(CucumberTestModel.getController().findUser(NHI) != null);
    }

    @Given("^There are no users in the system$")
    public void thereAreNoUsersInTheSystem() {
        CucumberTestModel.getController().getUsers().clear();
        assertTrue(CucumberTestModel.getController().getUsers().isEmpty());
    }

    @Given("^There exists a user with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void thereExistsAUserWithTheNHIFirstNameLastNameAndDateOfBirth(String NHI,
                                                                          String firstName, String lastName, String dateOfBirth) {
        CucumberTestModel.setUserNhi(NHI);
        if (CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()) == null) {
            CucumberTestModel.getController().getUsers().add(
                    new User(firstName, LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE), NHI));
        }
    }


    @Given("^a user with the NHI \"([^\"]*)\" does not exist$")
    public void aUserWithTheNHIDoesNotExist(String NHI) {
        CucumberTestModel.setUserNhi(NHI);
        CucumberTestModel.getController().getUsers().remove(AppController.getInstance().findUser(CucumberTestModel.getUserNhi()));
    }

    @Given("^The user sign up screen is loaded$")
    public void theSignUpScreenIsLoaded() {
        clickOn("#signUpButton");
    }

    @Given("^The login screen is loaded$")
    public void theLoginScreenIsLoaded() {
        /** The login screen is automatically loaded on startup **/
    }

    @Given("^The Create New Disease screen is loaded$")
    public void theCreateNewDiseaseScreenIsLoaded() {
        //Use default clinician
        clickOn("#clinicianTab");
        clickOn("#staffIdTextField");
        write("0");
        clickOn("#staffPasswordField");
        write("admin");
        clickOn("#loginCButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
        clickOn("#addDiseaseButton");
    }

    @Given("^they are registered to receive a \"([^\"]*)\"$")
    public void theyAreRegisteredToReceiveA(String organ) {
        Organs organToReceive = null;
        for (Organs value : Organs.values()) {
            if (organ.equalsIgnoreCase(value.toString())) {
                organToReceive = value;
            }
        }
        if (!CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).getReceiverDetails()
                .isCurrentlyWaitingFor(organToReceive)) {
            CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).getReceiverDetails().startWaitingForOrgan(organToReceive);
        }
    }

    @Given("^The user is alive$")
    public void theUserIsAlive() {
        if (CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).getDeceased()) {
            CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).setDateOfDeath(null);
        }
        assertFalse(CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).getDeceased());
    }

    @Given("^the cache is empty$")
    public void the_cache_is_empty() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^the cache is pre-populated$")
    public void the_cache_is_pre_populated() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^the app is logged in as a \"([^\"]*)\"$")
    public void the_app_is_logged_in_as_a(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^the \"([^\"]*)\" tab is selected$")
    public void the_tab_is_selected(String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
