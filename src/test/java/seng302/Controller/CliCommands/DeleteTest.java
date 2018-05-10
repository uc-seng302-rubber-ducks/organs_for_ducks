package seng302.Controller.CliCommands;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Scanner;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.CliCommands.DeleteDonor;
import seng302.Model.User;

public class DeleteTest {

  private AppController mockController = mock(AppController.class);
  private User testUser;
  private Scanner mockScanner = mock(Scanner.class);

  @Before
  public void CreateUser() {
    testUser = new User("testUser", LocalDate.of(1987, 4, 3), "ABC1234");
  }
  @Test
  public void UserCanBeDeleted() {
    String[] args = {"ABC1234"};
    DeleteDonor command = new DeleteDonor();
    when(mockScanner.next()).thenReturn("y");
    when(mockController.findUser("ABC1234")).thenReturn(testUser);
    command.setScanner(mockScanner);
    command.setController(mockController);
    new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    verify(mockController).deleteDonor(testUser);
  }

  @Test
  public void NonExistingUserCannotBeDeleted() {
    String[] args = {"ABC1234"};
    DeleteDonor command = new DeleteDonor();
    when(mockScanner.next()).thenReturn("y");
    when(mockController.findUser("ABC1234")).thenReturn(null);
    command.setScanner(mockScanner);
    command.setController(mockController);
    new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
    verify(mockController, times(0)).deleteDonor(testUser);
  }
}
