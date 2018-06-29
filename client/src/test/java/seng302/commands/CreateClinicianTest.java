package seng302.commands;

import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;
import seng302.controller.AppController;
import seng302.model.Clinician;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateClinicianTest {

    private AppController testController;

    @Before
    public void setUp() {
        testController = mock(AppController.class);
    }

    @Test
    public void testNoParams() {
        String[] args = {};
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(0)).updateClinicians(any());
    }

    @Test
    public void testInvalidInput() {
        String[] args = {"0", "", ""};
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(0)).updateClinicians(any());
    }

    @Test
    public void testValidInput() {
        String args[] = {"1", "name", "password", "region"};
        when(testController.getClinician("1")).thenReturn(null);
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(1)).updateClinicians(any());
    }

    @Test
    public void testDuplicateID() {
        String args[] = {"0", "B", "b", "B"};
        when(testController.getClinician("0")).thenReturn(new Clinician("0", "A", null, "A", "a"));
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(0)).updateClinicians(any());
    }


}
