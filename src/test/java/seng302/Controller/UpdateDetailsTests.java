package seng302.Controller;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import seng302.Controller.CliCommands.UpdateDetails;
import seng302.Model.User;

public class UpdateDetailsTests {

  AppController controller;
  DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
  int id = -1;
  @Before
  public void resetDonor() {


    controller = AppController.getInstance();
    controller.setUsers(new ArrayList<>());

    try {
      id = controller.Register("test dummy", LocalDate.parse("1111-11-11", sdf), "ABC1234");
      User user = controller.findUsers("test dummy").get(0);
      user.setWeight(65.3);
    }
    catch (Exception ex) {
      fail("Exception thrown setting up tests");
    }
  }

  @Test
  public void ShouldUpdateFirstName() {
    String[] args = {"-id="+id, "-f=Mal"};
    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User mal = controller.findUsers("Mal dummy").get(0);
    Assert.assertNotNull(mal);

    ArrayList<User> test = controller.findUsers("test dummy");
    assert(test.size() == 0);
  }

  @Test
  public void ShouldUpdateLastName() {
    String[] args = {"-id="+id, "-l=muppet"};
    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User muppet = controller.findUsers("test muppet").get(0);
    Assert.assertNotNull(muppet);

    ArrayList<User> test = controller.findUsers("test dummy");
    assert(test.size() == 0);
  }

  @Test
  public void ShouldUpdateFullName() {
    String[] args = {"-id="+id, "-f=stephen", "-l=hawking"};
    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User alan = controller.findUsers("stephen hawking").get(0);
    Assert.assertNotNull(alan);

    ArrayList<User> test = controller.findUsers("test dummy");
    assert(test.size() == 0);
  }

  @Test
  public void ShouldUpdateNumberField() {
    //height and weight are identical, no use testing both
    //just checking it can parse numbers
    String[] args = {"-id="+id, "-w=100"};
    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User test = controller.findUsers("test dummy").get(0);
    assert(test.getWeight() == 100);
  }

  @Test
  public void ShouldNotUpdateBadNumberField() {
    //height and weight are identical, no use testing both
    String[] args = {"-id="+id, "-w=fat"};
    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User test = controller.findUsers("test dummy").get(0);
    assert(test.getWeight() == 65.3);
  }

  @Test
  public void ShouldUpdateDateField() {
    //dob and dod are identical, no use testing both
    //just checking it can parse dates
    String[] args = {"-id="+id, "-dob=2020-03-04"};

    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User test = controller.findUsers("test dummy").get(0);
    try {
      assert (test.getDateOfBirth().equals(LocalDate.parse("2020-03-04",sdf)));
    }
    catch (DateTimeParseException ex) {
      fail("Could not parse date (error in tester)");
    }
  }

  @Test
  public void ShouldNotUpdateBadDate() {
    //dob and dod are identical, no use testing both
    String[] args = {"-id="+id, "-dob=1963"};

    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

      User test = controller.findUsers("test dummy").get(0);
    try {
      assert (test.getDateOfBirth().equals(LocalDate.parse("1111-11-11",sdf)));
    }
    catch (DateTimeParseException ex) {
      fail("Could not parse date (error in tester)");
    }

  }

  @Test
  public void ShouldUpdateLastModifiedTimestamp() throws InterruptedException{
    User user = controller.getUser(id);
    Thread.sleep(100);
    LocalDateTime oldTime = user.getLastModified();

    String[] args = {"-id="+id, "-f=fred"};

    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    LocalDateTime newTime = user.getLastModified();
    System.out.println(oldTime);
    System.out.println(newTime); // test needs delay removing these lines will cause the test to fail
    assert(newTime.isAfter(oldTime));
  }

  @Test
  public void ShouldNotUpdateNHItoDuplicateOfExistingUser() {
    //one user cannot have the NHI changed to that of another user
    fail("not yet implemented");
  }
}
