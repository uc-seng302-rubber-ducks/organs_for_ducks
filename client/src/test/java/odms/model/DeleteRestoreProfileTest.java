package odms.model;

import odms.bridge.AdministratorBridge;
import odms.bridge.ClinicianBridge;
import odms.bridge.UserBridge;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.controller.AppController;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class DeleteRestoreProfileTest {

    private AppController appC;
    private UserBridge userBridge = mock(UserBridge.class);
    private ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private AdministratorBridge administratorBridge = mock(AdministratorBridge.class);

    @Before
    public void setUp() {
        appC = AppController.getInstance();
    }

    @After
    public void tearDown() {
        AppController.setInstance(null);
    }

    @Test
    public void testDeleteUser() {
        User testUser = new User("Stan", LocalDate.of(2000, 3, 5), "ABC4321");
        doNothing().when(userBridge).deleteUser(any(User.class));
        appC.setUserBridge(userBridge);

        List<User> activeUsers = appC.getUsers();
        activeUsers.remove(testUser);
        activeUsers.add(testUser);

        appC.deleteUser(testUser);
        Assert.assertTrue(testUser.isDeleted());

    }

    @Test
    public void testDeleteClinician() {
        Clinician testClinician = new Clinician("Bob", "id", "1234");
        doNothing().when(clinicianBridge).deleteClinician(any(Clinician.class), anyString());
        appC.setClinicianBridge(clinicianBridge);

        List<Clinician> activeClinicians = appC.getClinicians();
        activeClinicians.remove(testClinician);
        activeClinicians.add(testClinician);

        appC.deleteClinician(testClinician);
        Assert.assertTrue(testClinician.isDeleted());
    }

    @Test
    public void testDeleteAdmin() {
        Administrator testAdmin = new Administrator("nameuser", "first", "middle", "last", "1234");
        doNothing().when(administratorBridge).deleteAdmin(any(Administrator.class), anyString());
        appC.setAdministratorBridge(administratorBridge);

        Collection<Administrator> activeAdmins = appC.getAdmins();
        activeAdmins.remove(testAdmin);
        activeAdmins.add(testAdmin);

        appC.deleteAdmin(testAdmin);
        Assert.assertTrue(testAdmin.isDeleted());
    }
}
