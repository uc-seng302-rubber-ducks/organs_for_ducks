package seng302.commands;

import odms.commands.UpdateClinician;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import picocli.CommandLine;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateClinicianTest {

    private AppController testController;
    private Clinician testClinician;
    private Clinician testClinician2;
    private UpdateClinician command;

    @Before
    public void setUp() {
        testController = mock(AppController.class);
        testClinician = mock(Clinician.class);
        testClinician2 = mock(Clinician.class);
        when(testController.getClinician("0")).thenReturn(testClinician);
        when(testController.getClinician("2")).thenReturn(testClinician2);
        command = new UpdateClinician();
        command.setController(testController);

    }


    @Test
    public void shouldUpdateId() {
        String[] args = {"0", "-id=1"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        verify(testClinician, times(1)).setStaffId("1");
    }

    @Test
    public void shouldUpdateMultipleAttributes() {
        String[] args = {"0", "-f=Buster", "-r=Canterbury", "-a=There"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        verify(testClinician, times(1)).setFirstName("Buster");
        verify(testClinician, times(1)).setRegion("Canterbury");
        verify(testClinician, times(1)).setWorkAddress("There");
    }

    @Ignore
    @Test
    public void shouldUpdateValidAttributesWhenInvalidGiven() {
        //TODO Test this after input validation is ready
        //if a bad
    }

    @Test
    public void shouldDoNothingWhenNoAttributesGiven() {
        String[] args = {"0"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        verify(testController, times(0)).updateClinicians(any());

    }

    @Test
    public void shouldNotBeAbleToHaveDuplicateClinicianId() {
        String[] args = {"0", "-id=2"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        verify(testClinician, times(0)).setStaffId(anyString());

    }

}
