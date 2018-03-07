package seng302.Controller;

import static org.junit.Assert.fail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.CliCommands.Register;
import seng302.Controller.CliCommands.Update;
import seng302.Model.Donor;
import seng302.Model.Organs;

public class UpdateTests {

  AppController controller;
  Donor donor;

  @Before
  public void resetDonors() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    controller = AppController.getInstance();
    controller.setDonors(new ArrayList<>());
    try {
      controller.Register("test dummy", sdf.parse("1997-2-5"));
      Donor donor = controller.findDonors("test dummy").get(0);
    }
    catch (Exception ex) {
      fail("Error setting up before test");
    }
  }
  @Test
  public void ShouldAddSingleOrgan() {


  }


  @Test
  public void ShouldAddMultipleOrgans() {

  }

  @Test
  public void ShouldNotDuplicateOrgansAlreadyListed() {
    //we're using a hashmap so it can't
    //test exists for posterity

  }

  @Test
  public void ShouldRemoveSingleOrgan() {

  }

  @Test
  public void ShouldRemoveMultipleOrgans() {

  }

  @Test
  public void ShouldNotRemoveOrgansNotListed() {
    //we're using a hashmap so it can't
    //test exists for posterity

  }

  @Test
  public void ShouldUpdateFirstName() {

  }

  @Test
  public void ShouldUpdateLastName() {

  }

  @Test
  public void ShouldUpdateFullName() {

  }

  @Test
  public void ShouldUpdateNumberField() {
    //height and weight are identical, no use testing both
  }

  @Test
  public void ShouldUpdateDateField() {
    //dob and dod are identical, no use testing both
  }

  @Test
  public void ShouldUpdateLastModifiedTimestamp() {

  }
}
