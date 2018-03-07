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

public class UpdateDetailsTests {


  @Test
  public void ShouldUpdateFirstName() {
    fail("not yet implemented");
  }

  @Test
  public void ShouldUpdateLastName() {
    fail("not yet implemented");
  }

  @Test
  public void ShouldUpdateFullName() {
    fail("not yet implemented");
  }

  @Test
  public void ShouldUpdateNumberField() {
    //height and weight are identical, no use testing both
    //just checking it can parse numbers
    fail("not yet implemented");
  }

  @Test
  public void ShouldUpdateDateField() {
    //dob and dod are identical, no use testing both
    //just checking it can parse dates
    fail("not yet implemented");
  }

  @Test
  public void ShouldUpdateLastModifiedTimestamp() {
    fail("not yet implemented");
  }
}
