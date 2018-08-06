package odms.commands;

import odms.bridge.AdministratorBridge;
import odms.bridge.ClinicianBridge;
import odms.bridge.UserBridge;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.view.IoHelper;
import org.junit.Before;
import org.junit.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;

public class ViewTest {

    private AppController appController;
    private UserBridge userBridge;
    private ClinicianBridge clinicianBridge;
    private AdministratorBridge administratorBridge;

    private Clinician testClinician;
    private User testUser;
    private Administrator testAdministrator;
    private View command;
    private IoHelper ioHelper;

    @Before
    public void setUp() throws IOException {
        appController = mock(AppController.class);
        userBridge = mock(UserBridge.class);
        clinicianBridge = mock(ClinicianBridge.class);
        administratorBridge = mock(AdministratorBridge.class);
        ioHelper = mock(IoHelper.class);
        String token = "aaaa";

        when(appController.getUserBridge()).thenReturn(userBridge);
        when(appController.getClinicianBridge()).thenReturn(clinicianBridge);
        when(appController.getAdministratorBridge()).thenReturn(administratorBridge);
        when(appController.getToken()).thenReturn(token);


        testClinician = new Clinician("16", "secure", "Affie", "Ali", "Al");
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        testUser = new User("Aa", LocalDate.parse("2000-01-20", sdf), "ABC1234");
        testAdministrator = new Administrator("admin1", "Anna", "Kate", "Robertson", "face");

        when(userBridge.getUser(anyString())).thenReturn(testUser);
        when(clinicianBridge.getClinician(anyString(),anyString())).thenReturn(testClinician);
        when(administratorBridge.getAdmin(anyString(),anyString())).thenReturn(testAdministrator);

        command = new View();
        command.setAppController(appController);
    }

    @Test
    public void testUserIsSelected() throws IOException {
        String[] args = {"ABC1234"};

        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(appController,times(1)).getUserBridge();
    }

    @Test
    public void testClinicianIsSelected(){
        String[] args = {"16","-c"};
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(appController,times(1)).getClinicianBridge();
    }

    @Test
    public void testAdminIsSelected(){
        String[] args = {"a", "-a"};
        new CommandLine(command).parseWithHandler(new CommandLine.RunLast(), System.err, args);
        verify(appController,times(1)).getAdministratorBridge();
    }
}
