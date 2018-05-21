package seng302.steps;

import static org.testfx.api.FxAssert.verifyThat;
import static seng302.Utils.TableViewsMethod.getCell;

import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import seng302.App;
import seng302.Controller.AppController;
import seng302.Model.User;


public class GivenSteps extends ApplicationTest{

  private AppController controller = AppController.getInstance();

  @After
  public void tearDown() throws TimeoutException {
    if (FxToolkit.isFXApplicationThreadRunning()) {
      FxToolkit.cleanupStages();
    }
  }

    @Given("^I have started the CLI$")
    public void iHaveStartedTheCLI() {
        controller = AppController.getInstance();
    }

    @Given("^I have started the GUI$")
    public void iHaveStartedTheGUI() throws Throwable {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
    }

    @Given("^a user with the NHI \"([^\"]*)\" exists$")
    public void aUserWithTheNHIExists(String NHI) {
      ArrayList<User> userList = controller.findUsers(NHI);
        if (userList.isEmpty()) {
          //controller.getUsers().add(new User(NHI, LocalDate.now()));
        }
    }

  @Given("^There are no users in the system$")
  public void thereAreNoUsersInTheSystem() {
        AppController.getInstance().getUsers().clear();
    }

  @Given("^There exists a user with the NHI \"([^\"]*)\", first name \"([^\"]*)\", last name \"([^\"]*)\" and date of birth \"([^\"]*)\"$")
  public void thereExistsAUserWithTheNHIFirstNameLastNameAndDateOfBirth(String NHI,
      String firstName, String lastName, String dateOfBirth) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

  @Given("^There exists a user with \"([^\"]*)\"$")
  public void thereExistsAUserWith(String NHI) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

  @Given("^a user with the NHI \"([^\"]*)\" does not exist$")
  public void aUserWithTheNHIDoesNotExist(String NHI) {
        AppController.getInstance().getUsers().remove(AppController.getInstance().findUser(NHI));
    }

  @Given("^The user sign up screen is loaded$")
  public void theSignUpScreenIsLoaded() {
        clickOn("#signUpButton");
    }

    @Given("^The login screen is loaded$")
    public void theLoginScreenIsLoaded() {

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

  @And("^they are registered to receive a \"([^\"]*)\"$")
  public void theyAreRegisteredToReceiveA(String arg0) throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    throw new PendingException();
  }

  @Given("^The user is alive$")
  public void theUserIsAlive() throws Throwable {
    // Write code here that turns the phrase above into concrete actions
    throw new PendingException();
  }
}
