package odms.commands;

import odms.commons.model.Clinician;
import odms.controller.AppController;
import odms.bridge.CountriesBridge;
import odms.view.CLI;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Mockito.*;

public class DeleteClinicianTest {

    private AppController mockController = mock(AppController.class);
    private CountriesBridge countriesBridge = mock(CountriesBridge.class);
    private Clinician testClinician;

    @Before
    public void CreateClinician() throws IOException {
        testClinician = new Clinician("frank", "3", "over there", "sydney", "password");
        when(countriesBridge.getAllowedCountries()).thenReturn(new HashSet());
        AppController.getInstance().setCountriesBridge(countriesBridge);
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
