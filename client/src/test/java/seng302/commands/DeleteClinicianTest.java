package seng302.commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.controller.AppController;
import seng302.model.Clinician;
import seng302.view.CLI;

import static org.mockito.Mockito.*;

public class DeleteClinicianTest {

    private AppController mockController = mock(AppController.class);
    private Clinician testClinician;

    @Before
    public void CreateClinician() {
        testClinician = new Clinician("frank", "3", "over there", "sydney", "password");
    }

    @Test
    public void UserCanBeDeleted() {
        String[] args = {"3"};
        DeleteClinician command = new DeleteClinician();
        when(mockController.getClinician("3")).thenReturn(testClinician);
        command.setController(mockController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController).deleteClinician(testClinician);
    }

    @Test
    public void NonExistingUserCannotBeDeleted() {
        String[] args = {"3"};
        DeleteClinician command = new DeleteClinician();
        when(mockController.findUser("3")).thenReturn(null);
        command.setController(mockController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        CLI.parseInput("y", mockController);
        verify(mockController, times(0)).deleteClinician(testClinician);
    }
}
