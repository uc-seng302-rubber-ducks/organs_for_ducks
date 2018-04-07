package seng302.Controller;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.CliCommands.UpdateRemoveOrgans;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.User;

public class UpdateRemoveTests {

  AppController controller;

  @Before
  public void resetDonors() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    controller = AppController.getInstance();
    controller.setUsers(new ArrayList<>());
    try {
      controller.Register("Three Organs", sdf.parse("1978-3-6"));
      controller.Register("One Organ", sdf.parse("1997-2-5"));

      User three = controller.findUsers("Three Organs").get(0);
      three.getDonorDetails().addOrgan(Organs.CONNECTIVE_TISSUE);
      three.getDonorDetails().addOrgan(Organs.MIDDLE_EAR);
      three.getDonorDetails().addOrgan(Organs.SKIN);

      User one = controller.findUsers("One Organ").get(0);
      one.getDonorDetails().addOrgan(Organs.LUNG);
    } catch (Exception ex) {
      fail("Error setting up before test");
    }
  }


  @Test
  public void ShouldRemoveSingleOrganLeavingEmpty() {

    String[] args = {"-f=One", "-l=Organ", "-dob=1997-2-5", "lung"};
    new CommandLine(new UpdateRemoveOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("One Organ").get(0);

    assert (user.getDonorDetails().getOrgans().size() == 0);
  }

  @Test
  public void ShouldRemoveSingleOrganLeavingOrgansRemaining() {
    String[] args = {"-f=Three", "-l=Organs", "-dob=1978-3-6", "skin"};
    new CommandLine(new UpdateRemoveOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("Three Organs").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();

    assert(list.contains(Organs.CONNECTIVE_TISSUE));
    assert(list.contains(Organs.MIDDLE_EAR));

    assert (user.getDonorDetails().getOrgans().size() == 2);
  }

  @Test
  public void ShouldRemoveMultipleOrgansLeavingEmpty() {
    String[] args = {"-f=Three", "-l=Organs", "-dob=1978-3-6", "skin", "connective_tissue", "middle_ear"};
    new CommandLine(new UpdateRemoveOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("Three Organs").get(0);
    assert (user.getDonorDetails().getOrgans().size() == 0);
  }

  @Test
  public void ShouldRemoveMultipleOrgansLeavingOrgansRemaining() {
    String[] args = {"-f=Three", "-l=Organs", "-dob=1978-3-6", "skin", "middle_ear"};
    new CommandLine(new UpdateRemoveOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("Three Organs").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();

    assert(list.contains(Organs.CONNECTIVE_TISSUE));
    assert (user.getDonorDetails().getOrgans().size() == 1);
  }

  @Test
  public void ShouldNotRemoveOrgansNotListed() {
    //we're using a hashmap so it can't
    //test exists for posterity

    String[] args = {"-f=One", "-l=Organ", "-dob=1997-2-5", "heart"};
    new CommandLine(new UpdateRemoveOrgans())
        .parseWithHandler(new CommandLine.RunLast(), System.err, args);

    User user = controller.findUsers("One Organ").get(0);
    HashSet<Organs> list = user.getDonorDetails().getOrgans();

    assert (list.contains(Organs.LUNG));
    assert (user.getDonorDetails().getOrgans().size() == 1);
  }
}
