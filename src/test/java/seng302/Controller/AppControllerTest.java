package seng302.Controller;

import java.util.Date;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.Donor;

public class AppControllerTest {

  private AppController controller;
  @Before
  public void setup() {
    controller = AppController.getInstance();
  }
  @Test
  public void ShouldAddDonorToListWhenUserRegistered() {
    //Arrange
    Donor donor = new Donor("Frank", new Date(1,2,3));
    //Act
    controller.Register("Frank", new Date(1,2,3));
    //Assert
    Assert.assertTrue(controller.getDonors().contains(donor));
  }

  @Test
  public void ShouldAddDonorToListWhenUserRegisteredFullDetail() {
    //Arrange
    Donor donor = new Donor("Geoff", new Date(1,2,3));
    donor.setGender("m");
    donor.setHeight(1.85);
    donor.setWeight(90);
    donor.setDateOfDeath(new Date(2,3,4));
    donor.setBloodType("O-");
    donor.setCurrentAddress("42 wallaby way");
    donor.setRegion("Sydney");
    //Act
    controller.Register("Geoff", new Date(1,2,3), new Date(2,3,4), "m",
        1.85, 90, "O-", "42 wallaby way", "Sydney");
    //Assert
    Assert.assertTrue(controller.getDonors().contains(donor));

  }
}