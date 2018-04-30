package seng302.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import javafx.stage.Stage;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.User;

import java.util.ArrayList;

import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Controller.TableViewsMethod.getCell;

public class GivenSteps extends ApplicationTest{
    private AppController controller;

    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage);
    }

    @Given("^I have started the CLI$")
    public void iHaveStartedTheCLI() throws Throwable {
        controller = AppController.getInstance();
    }

    @Given("^I have started the GUI$")
    public void iHaveStartedTheGUI() throws Throwable {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        //AppController.getInstance().getUsers().clear();
    }

    @Given("^a user with the NHI \"([^\"]*)\" exists$")
    public void aUserWithTheNHIExists(String NHI) throws Throwable {
        ArrayList<User> userList = controller.findUsers("NHI");
        if (userList.isEmpty()) {
            //controller.getUsers().add(new User(NHI, new Date()));
        }
    }

    @Given("^There are no donors in the system$")
    public void thereAreNoDonorsInTheSystem() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^There exists a donor with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
    public void thereExistsADonorWithTheNHIFirstNameLastNameAndDateOfBirth(String NHI, String firstName, String lastName, String dateOfBirth) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^There exists a donor with \"([^\"]*)\"$")
    public void thereExistsADonorWith(String NHI) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^a donor with the NHI \"([^\"]*)\" does not exist$")
    public void aDonorWithTheNHIDoesNotExist(String NHI) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^The sign up screen is loaded$")
    public void theSignUpScreenIsLoaded() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^The login screen is loaded$")
    public void theLoginScreenIsLoaded() throws Throwable {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("^The Create New Disease screen is loaded$")
    public void theCreateNewDiseaseScreenIsLoaded() throws Throwable {
        //Use default clinician
        clickOn("#changeLogin");
        clickOn("#userIDTextField");
        write("0");
        clickOn("#passwordField");
        write("admin");
        clickOn("#loginButton");
        verifyThat("#staffIdLabel", LabeledMatchers.hasText("0"));
        clickOn("#searchTab");
        doubleClickOn(getCell("#searchTableView", 0, 0));
        clickOn("#diseaseTab");
        clickOn("#addDiseaseButton");
    }
}