package seng302.steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import seng302.Controller.AppController;
import seng302.Model.User;

import java.util.ArrayList;
import java.util.Date;

public class GivenSteps {
    private AppController controller;

    @Given("^I have started the CLI$")
    public void iHaveStartedTheCLI() throws Throwable {
        controller = AppController.getInstance();
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

}
