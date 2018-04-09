package seng302.Controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.Donor;
import seng302.Model.User;

public class AppControllerTest {

  private AppController controller;
  @Before
  public void setup() {
    controller = AppController.getInstance();
  }
  @Test
  public void ShouldAddDonorToListWhenUserRegistered() {
    //Arrange
    User user = new User("Frank", LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d")));
    //Act
    controller.Register("Frank",LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))));
    //Assert
    Assert.assertTrue(controller.getUsers().contains(user));
  }

  @Test
  public void ShouldAddDonorToListWhenUserRegisteredFullDetail() {
    //Arrange
    User user = new User("Geoff",LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))));
    user.setGender("m");
    user.setHeight(1.85);
    user.setWeight(90);
    user.setDateOfDeath(LocalDate.parse("2 3 4",(DateTimeFormatter.ofPattern("y M d"))));
    user.setBloodType("O-");
    user.setCurrentAddress("42 wallaby way");
    user.setRegion("Sydney");
    //Act
    controller.Register("Geoff", LocalDate.parse("1 2 3",(DateTimeFormatter.ofPattern("y M d"))),LocalDate.parse("2 3 4",(DateTimeFormatter.ofPattern("y M d"))), "m",
        1.85, 90, "O-", "42 wallaby way", "Sydney");
    //Assert
    Assert.assertTrue(controller.getUsers().contains(user));

  }
}
