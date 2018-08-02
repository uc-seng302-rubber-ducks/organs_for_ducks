package odms.commands;

import odms.controller.AppController;
import odms.commons.model.User;
import odms.utils.UserBridge;
import odms.view.CLI;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class DeleteUserTest {

    private AppController mockController;
    private User testUser;
    private DeleteUser command;
    private UserBridge userBridge;

    @Before
    public void CreateUser() {
        mockController = mock(AppController.class);
        testUser = new User("testUser", LocalDate.of(1987, 4, 3), "ABC1234");
        command = new DeleteUser();
        userBridge = mock(UserBridge.class);
        when(mockController.getUserBridge()).thenReturn(userBridge);
        command.setController(mockController);
    }

    @Test
    public void UserCanBeDeleted() throws IOException {
        String[] args = {"ABC1234"};

        when(userBridge.getUser(testUser.getNhi())).thenReturn(testUser);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController).deleteUser(testUser);
        assert (!userBridge.getExists(testUser.getNhi()));
    }

    @Test
    public void NonExistingUserCannotBeDeleted() throws IOException {
        String[] args = {"ABC1234"};

        when(userBridge.getUser(testUser.getNhi())).thenReturn(null);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController, times(0)).deleteUser(testUser);
    }
}
