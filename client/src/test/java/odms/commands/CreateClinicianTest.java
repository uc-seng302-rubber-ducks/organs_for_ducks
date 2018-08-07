package odms.commands;

import odms.bridge.ClinicianBridge;
import odms.commons.model.Clinician;
import odms.controller.AppController;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreateClinicianTest {

    private AppController testController;

    @Before
    public void setUp() {
        testController = mock(AppController.class);
        when(testController.getClinicianBridge()).thenReturn(mock(ClinicianBridge.class));
        when(testController.getClinicianBridge().getExists(anyString())).thenReturn(false);
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
        when(testController.getClinicianBridge().getExists(anyString())).thenReturn(true);
        String args[] = {"0", "B", "b", "B"};
        when(testController.getClinician("0")).thenReturn(new Clinician("0", "A", "Anna", "A", "a"));
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(0)).updateClinicians(any());
    }


}
