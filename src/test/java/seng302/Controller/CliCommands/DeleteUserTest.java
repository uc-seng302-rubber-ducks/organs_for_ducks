package seng302.Controller.CliCommands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Model.User;
import seng302.View.CLI;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class DeleteUserTest {

    private AppController mockController = mock(AppController.class);
    private User testUser;

    @Before
    public void CreateUser() {
        testUser = new User("testUser", LocalDate.of(1987, 4, 3), "ABC1234");
    }

    @Test
    public void UserCanBeDeleted() {
        String[] args = {"ABC1234"};
        DeleteUser command = new DeleteUser();
        when(mockController.findUser("ABC1234")).thenReturn(testUser);
        command.setController(mockController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController).deleteUser(testUser);
    }

    @Test
    public void NonExistingUserCannotBeDeleted() {
        String[] args = {"ABC1234"};
        DeleteUser command = new DeleteUser();
        when(mockController.findUser("ABC1234")).thenReturn(null);
        command.setController(mockController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController, times(0)).deleteUser(testUser);
    }
}
