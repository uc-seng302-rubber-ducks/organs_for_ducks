package odms.controller;

import odms.commons.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AppControllerTest {

    private AppController controller;

    @Before
    public void setup() {
        controller = AppController.getInstance();
    }

    @Test
    public void ShouldAddUserToListWhenUserRegistered() {
        //Arrange
        User user = new User("Frank", LocalDate.parse("1 2 3", (DateTimeFormatter.ofPattern("y M d"))),
                "ABC1234");
        //Act
        controller.addUser(user);
        //Assert
        Assert.assertTrue(controller.getUsers().contains(user));
    }

}
