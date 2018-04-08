package seng302.Controller;

import java.util.Date;
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
    User user = new User("Frank", new Date(1,2,3));
    //Act
    controller.Register("Frank", new Date(1,2,3));
    //Assert
    Assert.assertTrue(controller.getUsers().contains(user));
  }

  @Test
  public void ShouldAddDonorToListWhenUserRegisteredFullDetail() {
    //Arrange
    User user = new User("Geoff", new Date(1,2,3));
    user.setGender("m");
    user.setHeight(1.85);
    user.setWeight(90);
    user.setDateOfDeath(new Date(2,3,4));
    user.setBloodType("O-");
    user.setCurrentAddress("42 wallaby way");
    user.setRegion("Sydney");
    //Act
    controller.Register("Geoff", new Date(1,2,3), new Date(2,3,4), "m",
        1.85, 90, "O-", "42 wallaby way", "Sydney");
    //Assert
    Assert.assertTrue(controller.getUsers().contains(user));

  }
}