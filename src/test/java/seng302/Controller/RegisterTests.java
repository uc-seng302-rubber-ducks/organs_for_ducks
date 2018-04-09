package seng302.Controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.CliCommands.Register;
import seng302.Model.Donor;
import seng302.Model.User;

public class RegisterTests {

  AppController controller;
  DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  User minInfo;
  User maxInfo;
  @Before
  public void setup() {
    controller = AppController.getInstance();
    controller.setUsers(new ArrayList<>()); //reset donor list between tests

      minInfo = new User("John Doe", LocalDate.parse("1961-02-12",format));
      maxInfo = new User("Gus Johnson", LocalDate.parse("1990-04-03",format));
      maxInfo.setDateOfDeath(LocalDate.parse("2010-05-16",format));
      maxInfo.setHeight(1.85);
      maxInfo.setWeight(86.3);
      maxInfo.setGender("m");
      maxInfo.setRegion("Sydney");
      maxInfo.setCurrentAddress("42-wallaby-way");

  }

  @Test
  public void ShouldRegisterDonorWithMinimumInfo() {
    String[] args = {"John", "Doe", "1961-02-12"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);

    Assert.assertTrue(controller.getUsers().contains(minInfo));
  }

  @Test
  public void ShouldRegisterDonorWithMaximumInfo() {
    String[] args = {"Gus", "Johnson", "1990-04-03", "-dod=2010-05-16", "-he=1.85", "-w=86.3",
        "-g=m", "-addr=42-wallaby-way", "-r=Sydney"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    User registered = null;
    registered = controller.findUser("Gus Johnson", LocalDate.parse("1990-04-03",format));
    Assert.assertEquals(maxInfo, registered); //checks name and dob
    Assert.assertEquals(maxInfo.getDateOfDeath(), registered.getDateOfDeath());
    Assert.assertTrue(maxInfo.getHeight() == registered.getHeight());
    Assert.assertTrue(maxInfo.getWeight() == registered.getWeight());
    Assert.assertEquals(maxInfo.getGender(), registered.getGender());
    Assert.assertEquals(maxInfo.getCurrentAddress(), registered.getCurrentAddress());
    Assert.assertEquals(maxInfo.getRegion(), registered.getRegion());
    //Assert.assertEquals(maxInfo.toString(), registered.toString()); //includes time created/modified, so unusable
  }

  @Test
  public void ShouldNotRegisterWhenMissingParameters() {
    String[] args = {"Robert"};

    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getUsers().size() == 0);
  }

  @Test
  public void ShouldNotRegisterWhenMalformedParameters() {
    String[] args = {"Frank", "Sinatra", "1967"}; //invalid date
    //TODO seems to accept garbage dates as long as they are in some-dashed-format
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getUsers().size() == 0);
  }

  @Test
  public void ShouldNotRegisterWhenMalformedOptions() {
    String[] args = {"Ryan", "Clark", "1967-21-03", "-he=myheight"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getUsers().size() == 0);
  }

  @Test
  public void ShouldCancelRegistrationWhenHelpFlagPresent() {
    String[] args = {"Les", "Claypool", "1967-21-03", "-h"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getUsers().size() == 0);
  }
}
