package odms.commands;

import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.controller.AppController;
import odms.utils.ClinicianBridge;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UpdateClinicianTest {

    private AppController testController;
    private Clinician testClinician;
    private Clinician testClinician2;
    private UpdateClinician command;
    private ClinicianBridge clinicianBridge;

    @Before
    public void setUp() throws ApiException {
        testController = mock(AppController.class);
        testClinician = new Clinician();
        testClinician.setStaffId("0");
        clinicianBridge = mock(ClinicianBridge.class);
        when(testController.getClinicianBridge()).thenReturn(clinicianBridge);
        when(testController.getToken()).thenReturn(anyString());
        when(clinicianBridge.getClinician(anyString(),"token")).thenReturn(testClinician);

        command = new UpdateClinician();
        command.setController(testController);

    }


    @Test
    public void shouldUpdateId() {
        when(clinicianBridge.getExists(anyString())).thenReturn(false);
        String[] args = {"0", "-newID=1"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        assert (testClinician.getStaffId().equals("1"));
    }

    @Test
    public void shouldUpdateMultipleAttributes() {
        String[] args = {"0", "-f=Buster", "-r=Canterbury", "-s=There"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        assert (testClinician.getFirstName().equals("Buster"));
        assert (testClinician.getRegion().equals("Canterbury"));
        assert (testClinician.getStreetName().equals("There"));
    }

    @Test
    public void shouldNotSaveClinicianWhenInvalidAttributeGiven() {
        String[] args = {"0", "-f=inval^d", "-r=region"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        try {
            verify(testController, times(0)).saveClinician(testClinician);
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }

    @Test
    public void shouldSaveClinicianWhenValidAttributesGiven() {
        String[] args = {"0", "-f=valid", "-r=region"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        try {
            verify(testController, times(1)).saveClinician(testClinician);
        } catch (IOException e) {
            fail("IOException should not be thrown");
        }
    }

    @Test
    public void shouldDoNothingWhenNoAttributesGiven() throws IOException {
        String[] args = {"0"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);

        verify(testController, times(0)).saveClinician(any());

    }

    @Test
    public void shouldNotBeAbleToHaveDuplicateClinicianId() {
        when(clinicianBridge.getExists(anyString())).thenReturn(true);
        String[] args = {"0", "-newID=2"};
        new CommandLine(command)
                .parseWithHandler(new CommandLine.RunLast(), System.err, args);
        assert (testClinician.getStaffId().equals("0"));

    }

}
