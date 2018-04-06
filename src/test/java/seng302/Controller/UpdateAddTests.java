package seng302.Controller;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.CliCommands.UpdateAddOrgans;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.User;

public class UpdateAddTests {

  AppController controller;

  @Before
  public void resetDonors() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    controller = AppController.getInstance();
    controller.setUsers(new ArrayList<>());
    try {
      controller.Register("No Organs", sdf.parse("1978-3-6"));
      controller.Register("One Organ", sdf.parse("1997-2-5"));
      User user = controller.findUsers("One Organ").get(0);
      user.getDonorDetails().addOrgan(Organs.LUNG);
    } catch (Exception ex) {
      fail("Error setting up before test");
    }
  }

  @Test
  public void ShouldAddSingleOrgan() {
    String[] args = {"-f=No", "-l=Organs", "-dob=1978-3-6", "kidney"};
    new CommandLine(new UpdateAddOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("No Organs").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();
    if (list == null) {
      fail("user has empty organ set");
    }

    assert (list.contains(Organs.KIDNEY));
    assert (user.getDonorDetails().getOrgans().size() == 1);
  }

  @Test
  public void ShouldAddMultipleOrgans() {
    String[] args = {"-f=No", "-l=Organs", "-dob=1978-3-6", "kidney", "bone_marrow", "heart"};
    new CommandLine(new UpdateAddOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("No Organs").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();
    if (list == null) {
      fail("user has empty organ set");
    }

    assert (list.contains(Organs.KIDNEY));
    assert (list.contains(Organs.BONE_MARROW));
    assert (list.contains(Organs.HEART));
    assert (user.getDonorDetails().getOrgans().size() == 3);

  }

  @Test
  public void ShouldNotDuplicateOrgansAlreadyListed() {
    //we're using a hashmap so it can't
    //test exists for posterity
    String[] args = {"-f=One", "-l=Organ", "-dob=1997-2-5", "lung"};
    new CommandLine(new UpdateAddOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("One Organ").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();
    if (list == null) {
      fail("user has empty organ set");
    }

    assert (list.contains(Organs.LUNG));
    assert (user.getDonorDetails().getOrgans().size() == 1);
  }

  @Test
  public void ShouldNotDuplicateOrgansAlreadyListeedMultipleAdded() {
    String[] args = {"-f=One", "-l=Organ", "-dob=1997-2-5", "intestine", "lung", "cornea"};
    new CommandLine(new UpdateAddOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("One Organ").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();
    if (list == null) {
      fail("user has empty organ set");
    }

    assert (list.contains(Organs.LUNG));
    assert (list.contains(Organs.INTESTINE));
    assert (list.contains(Organs.CORNEA));
    assert (user.getDonorDetails().getOrgans().size() == 3);
  }
}
