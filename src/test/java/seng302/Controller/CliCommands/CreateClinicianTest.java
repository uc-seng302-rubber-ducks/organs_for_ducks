package seng302.Controller.CliCommands;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.CliCommands.CreateClinician;
import seng302.Model.Clinician;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Ignore
    @Test
    public void testInvalidInput() {
        String[] args = {};
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        //verify(testController, times(0)).updateClinicians(any());

        Assert.fail("not yet implemented"); //TODO Need re-engineering for global inout validation
    }

    @Ignore
    @Test
    public void testValidInput() {
        String args[] = {"0", "name", "password", "region"};
        when(testController.getClinician("0")).thenReturn(null);
        CreateClinician command = new CreateClinician();
        command.setController(testController);
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(testController, times(1)).updateClinicians(any());

        //Assert.fail("not yet implemented"); //TODO implement when input validation added
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
