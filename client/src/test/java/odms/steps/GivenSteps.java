package odms.steps;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import odms.App;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import org.mockito.AdditionalMatchers;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeoutException;

import static odms.TestUtils.FxRobotHelper.clickOnButton;
import static odms.TestUtils.FxRobotHelper.setTextField;
import static odms.TestUtils.TableViewsMethod.getCell;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testfx.api.FxAssert.verifyThat;


public class GivenSteps extends ApplicationTest {

    AppController controller = AppController.getInstance();
    String userNhi = null;

    @After
    public void tearDown() throws TimeoutException {
        if (FxToolkit.isFXApplicationThreadRunning()) {
            FxToolkit.cleanupStages();
        }
        AppController.setInstance(null);
    }

    @Given("^I have started the CLI$")
    public void iHaveStartedTheCLI() {
        controller = mock(AppController.class);
    }

    @Given("^I have started the GUI$")
    public void iHaveStartedTheGUI() throws Throwable {
        AppController.setInstance(CucumberTestModel.getController());
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
    }

    @Given("^a user with the NHI \"([^\"]*)\" exists$")
    public void aUserWithTheNHIExists(String NHI) throws IOException {
        CucumberTestModel.setUserNhi(NHI);
        if (CucumberTestModel.getController().findUser(NHI) == null) {
            User user = new User("A", LocalDate.now().minusYears(20), NHI);

            when(CucumberTestModel.getUserBridge().getUser(eq(NHI))).thenReturn(user);
            CucumberTestModel.setUser(user);
        }
        assertTrue(CucumberTestModel.getController().findUser(NHI) != null);
    }

    @Given("^There are no users in the system$")
    public void thereAreNoUsersInTheSystem() throws IOException {
        CucumberTestModel.getController().getUsers().clear();
        when(CucumberTestModel.getUserBridge().getUser(anyString())).thenReturn(null);
        assertTrue(CucumberTestModel.getController().getUsers().isEmpty());
    }

    @Given("^There exists a user with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void thereExistsAUserWithTheNHIFirstNameLastNameAndDateOfBirth(String NHI,
                                                                          String firstName, String lastName, String dateOfBirth) throws IOException {
        CucumberTestModel.setUserNhi(NHI);
        User user = new User(firstName, LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE), NHI);
        CucumberTestModel.getController().addUser(user);

        if (CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()) == null) {
            when(CucumberTestModel.getUserBridge().getUser(eq(NHI))).thenReturn(user);
        }
    }


    @Given("^a user with the NHI \"([^\"]*)\" does not exist$")
    public void aUserWithTheNHIDoesNotExist(String NHI) throws IOException {
        CucumberTestModel.setUserNhi(NHI);
        when(CucumberTestModel.getUserBridge().getUser(eq(NHI))).thenReturn(null);
        CucumberTestModel.getController().getUsers().remove(AppController.getInstance().findUser(CucumberTestModel.getUserNhi()));
    }

    @Given("^The user sign up screen is loaded$")
    public void theSignUpScreenIsLoaded() {
        clickOn("#signUpButton");
    }

    @Given("^The login screen is loaded$")
    public void theLoginScreenIsLoaded() {
        // The login screen is automatically loaded on startup
    }

    @Given("^The Create New Disease screen is loaded$")
    public void theCreateNewDiseaseScreenIsLoaded() throws IOException {
        when(CucumberTestModel.getClinicianBridge().getClinician(anyString(), anyString())).thenReturn(
                new Clinician("", "0", "")
        );
        when(CucumberTestModel.getTransplantBridge().getWaitingList(anyInt(), anyInt(), anyString(), anyString(), anyCollection())).thenReturn(new ArrayList<>());
        when(CucumberTestModel.getUserBridge().loadUsersToController(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString())).thenReturn(Collections.singletonList(UserOverview.fromUser(CucumberTestModel.getUser())));
        when(CucumberTestModel.getLoginBridge().loginToServer(anyString(), anyString(), anyString())).thenReturn("FakeToken");
        //Use default clinician
        clickOn("#clinicianTab");
        setTextField(this, "#staffIdTextField", "0");
        setTextField(this, "#staffPasswordField", "admin");
        clickOnButton(this, "#loginCButton");
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
        CucumberTestModel.getMedicationInteractionCache().clear();
    }

    @Given("^the cache is pre-populated$")
    public void the_cache_is_pre_populated() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        CucumberTestModel.getHttpRequester().getDrugInteractions("Xanax", "Codeine");
        CucumberTestModel.getHttpRequester().getDrugInteractions("Aceon", "pancreaze");
        CucumberTestModel.getHttpRequester().getDrugInteractions("Aceon", "Codeine");
        CucumberTestModel.getHttpRequester().getDrugInteractions("Aceon", "Xanax");
        CucumberTestModel.getMedicationInteractionCache().get("Aceon", "pancreaze").setDateTime(LocalDateTime.now().minusDays(3));
    }

    @Given("^the app is logged in as a \"([^\"]*)\"$")
    public void the_app_is_logged_in_as_a(String User) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        iHaveStartedTheGUI();
        aUserWithTheNHIExists(User);
        setTextField(this, "#userIDTextField", User);
        clickOnButton(this, "#loginUButton");
    }

    @Given("^the user is taking \"([^\"]*)\" and \"([^\"]*)\"$")
    public void the_user_is_taking_and(String drugA, String drugB) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).addCurrentMedication(drugA);
        CucumberTestModel.getController().findUser(CucumberTestModel.getUserNhi()).addCurrentMedication(drugB);
    }


    @Given("^the \"([^\"]*)\" tab is selected$")
    public void the_tab_is_selected(String tab) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        clickOn("#" + tab);
    }

    @Given("^a clinician with Staff Id \"([^\"]*)\" and password \"([^\"]*)\" exists$")
    public void aClinicianWithStaffIdAndPasswordExists(String arg0, String arg1) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        when(CucumberTestModel.getLoginBridge().loginToServer(eq(arg0), eq(arg1), anyString())).thenReturn("haHAA");
        when(CucumberTestModel.getLoginBridge().loginToServer(eq(arg0), AdditionalMatchers.not(eq(arg1)), anyString())).thenThrow(new ApiException(401, "could not log in as the requested user"));
        when(CucumberTestModel.getClinicianBridge().getClinician(eq(arg0), anyString())).thenReturn(new Clinician("", arg0, arg1));
    }

    @Given("^a clinician with staff id \"([^\"]*)\" does not exist$")
    public void aClinicianWithStaffIdDoesNotExist(String arg0) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        when(CucumberTestModel.getLoginBridge().loginToServer(anyString(), anyString(), anyString())).thenReturn("haHAA");
        when(CucumberTestModel.getClinicianBridge().getClinician(eq(arg0), anyString())).thenReturn(null);
    }
}
