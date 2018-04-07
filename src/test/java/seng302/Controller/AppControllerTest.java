package seng302.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    Donor donor = new Donor("Frank", LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))));
    //Act
    controller.Register("Frank",LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))));
    //Assert
    Assert.assertTrue(controller.getDonors().contains(donor));
  }

  @Test
  public void ShouldAddDonorToListWhenUserRegisteredFullDetail() {
    //Arrange
    Donor donor = new Donor("Geoff",LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))));
    donor.setGender("m");
    donor.setHeight(1.85);
    donor.setWeight(90);
    donor.setDateOfDeath(LocalDate.parse("2 3 4",(DateTimeFormatter.ofPattern("y M d"))));
    donor.setBloodType("O-");
    donor.setCurrentAddress("42 wallaby way");
    donor.setRegion("Sydney");
    //Act
    controller.Register("Geoff", LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))),LocalDate.parse("2 3 4",(DateTimeFormatter.ofPattern("y M d"))), "m",
        1.85, 90, "O-", "42 wallaby way", "Sydney");
    //Assert
    Assert.assertTrue(controller.getDonors().contains(donor));

  }
}
