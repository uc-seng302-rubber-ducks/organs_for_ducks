/*
package seng302.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.CliCommands.Register;
import seng302.Model.Donor;

public class RegisterTests {

  AppController controller;
  Donor minInfo;
  Donor maxInfo;
  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  @Before
  public void setup() {
    controller = AppController.getInstance();
    controller.setDonors(new ArrayList<>()); //reset donor list between tests
 */
/*   try {
      minInfo = new Donor("John Doe", sdf.parse("1961-2-12"));
      maxInfo = new Donor("Gus Johnson", sdf.parse("1990-4-3"));
      maxInfo.setDateOfDeath(sdf.parse("2010-5-16"));
      maxInfo.setHeight(1.85);
      maxInfo.setWeight(86.3);
      maxInfo.setGender("m");
      maxInfo.setRegion("Sydney");
      maxInfo.setCurrentAddress("42-wallaby-way");
    }
    catch (ParseException ex) {
      Assert.fail("Error in test setup. test Donor dates not parsed correctly");
    }*//*

  }

  @Test
  public void ShouldRegisterDonorWithMinimumInfo() {
    String[] args = {"John", "Doe", "1961-2-12"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);

    Assert.assertTrue(controller.getDonors().contains(minInfo));
  }

  @Test
  public void ShouldRegisterDonorWithMaximumInfo() {
    String[] args = {"Gus", "Johnson", "1990-04-03", "-dod=2010-5-16", "-he=1.85", "-w=86.3",
        "-g=m", "-addr=42-wallaby-way", "-r=Sydney"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Donor registered = null;
    try {
      registered = controller.findDonor("Gus Johnson", sdf.parse("1990-04-03"));
    } catch (ParseException ex) {
      Assert.fail();
    }
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
    Assert.assertTrue(controller.getDonors().size() == 0);
  }

  @Test
  public void ShouldNotRegisterWhenMalformedParameters() {
    String[] args = {"Frank", "Sinatra", "1967"}; //invalid date
    //TODO seems to accept garbage dates as long as they are in some-dashed-format
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getDonors().size() == 0);
  }

  @Test
  public void ShouldNotRegisterWhenMalformedOptions() {
    String[] args = {"Ryan", "Clark", "1967-21-3", "-he=myheight"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getDonors().size() == 0);
  }

  @Test
  public void ShouldCancelRegistrationWhenHelpFlagPresent() {
    String[] args = {"Les", "Claypool", "1967-21-3", "-h"};
    new CommandLine(new Register()).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    Assert.assertTrue(controller.getDonors().size() == 0);
  }
}
*/
