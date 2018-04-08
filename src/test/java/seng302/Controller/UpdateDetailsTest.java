package seng302.Controller;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import seng302.Controller.CliCommands.UpdateDetails;
import seng302.Model.Donor;
import seng302.Model.User;

public class UpdateDetailsTest {

  AppController controller;
  SimpleDateFormat sdf;
  int id = -1;
  @Before
  public void resetDonor() {
    sdf = new SimpleDateFormat("yyyy-MM-dd");

    controller = AppController.getInstance();
    controller.setUsers(new ArrayList<>());

    try {
      id = controller.Register("test dummy", sdf.parse("1111-11-11"));
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
    String[] args = {"-id="+id, "-dob=2020-3-4"};

    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User test = controller.findUsers("test dummy").get(0);
    try {
      assert (test.getDateOfBirth().equals(sdf.parse("2020-3-4")));
    }
    catch (ParseException ex) {
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
      assert (test.getDateOfBirth().equals(sdf.parse("1111-11-11")));
    }
    catch (ParseException ex) {
      fail("Could not parse date (error in tester)");
    }

  }

  @Test
  public void ShouldUpdateLastModifiedTimestamp() throws InterruptedException{
    User user = controller.getUser(id);
    Thread.sleep(100);
    DateTime oldTime = user.getLastModified();

    String[] args = {"-id="+id, "-f=fred"};

    new CommandLine(new UpdateDetails())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    DateTime newTime = user.getLastModified();
    System.out.println("New time: "+newTime);
    System.out.println("Old time: "+ oldTime);
    assert(newTime.isAfter(oldTime));
  }
}
