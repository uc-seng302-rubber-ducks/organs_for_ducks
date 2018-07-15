package odms.commands;

import odms.controller.AppController;
import odms.commons.model.User;
import odms.commons.model.datamodel.Address;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class CreateUserTest {

    AppController controller;
    private DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private User minInfo;
    private User maxInfo;

    @Before
    public void setup() {
        controller = AppController.getInstance(); //TODO use mocks instead of actual instance  noted: 24/6,
        controller.setUsers(new ArrayList<>()); //reset users list between tests

        minInfo = new User("John Doe", LocalDate.parse("1961-02-12", format), "ABC1234");
        maxInfo = new User("Gus Johnson", LocalDate.parse("1990-04-03", format), "BCD2345");
        maxInfo.setDateOfDeath(LocalDate.parse("2010-05-16", format));
        maxInfo.setHeight(1.85);
        maxInfo.setWeight(86.3);
        maxInfo.setBirthGender("m");
        maxInfo.getContactDetails().setAddress(new Address("42", "wallaby-way", "", "", "Sydney", "", ""));
    }

    //<editor-fold>
    @Test
    public void ShouldRegisterDonorWithMinimumInfo() {
        String[] args = {"John", "Doe", "ABC1234", "1961-02-12"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);

        assertTrue(controller.getUsers().contains(minInfo));
    }

    @Test
    public void ShouldRegisterDonorWithMaximumInfo() {
        String[] args = {"Gus", "Johnson", "BCD2345", "1990-04-03", "-dod=2010-05-16", "-he=1.85",
                "-w=86.3",
                "-g=m", "-n=42", "-s=wallaby-way", "-r=Sydney"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        User registered = null;
        registered = controller.findUser("Gus Johnson", LocalDate.parse("1990-04-03", format));
        Assert.assertEquals(maxInfo, registered); //checks name and dob
        Assert.assertEquals(maxInfo.getDateOfDeath(), registered.getDateOfDeath());
        assertTrue(maxInfo.getHeight() == registered.getHeight());
        assertTrue(maxInfo.getWeight() == registered.getWeight());
        Assert.assertEquals(maxInfo.getBirthGender(), registered.getBirthGender());
        Assert.assertEquals(maxInfo.getContactDetails().getAddress().toString(), registered.getContactDetails().getAddress().toString());
        Assert.assertEquals(maxInfo.getRegion(), registered.getRegion());
    }

    @Test
    public void ShouldNotRegisterWhenMissingParameters() {
        String[] args = {"Robert"};

        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().size() == 0);
    }

    @Test
    public void ShouldNotRegisterWhenMalformedParameters() {
        String[] args = {"Frank", "Sinatra", "abcd-as-sa"}; //invalid date
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().size() == 0);
    }

    @Test
    public void ShouldNotRegisterWhenMalformedOptions() {
        String[] args = {"Ryan", "Clark", "1967-21-03", "-he=myheight"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().size() == 0);
    }

    @Test
    public void ShouldCancelRegistrationWhenHelpFlagPresent() {
        String[] args = {"Les", "Claypool", "1967-21-03", "-h"};
        new CommandLine(new CreateUser()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assertTrue(controller.getUsers().size() == 0);
    }
}
