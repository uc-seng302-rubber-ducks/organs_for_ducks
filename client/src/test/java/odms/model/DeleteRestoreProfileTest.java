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
//        testUser = new User("Stan", LocalDate.of(2000, 3, 5), "ABC4321");
//        testClinician = new Clinician("Bob", "id", "1234");
//        testAdmin = new Administrator("nameuser", "first", "middle", "last", "1234");
        AppController.setInstance(null);
        appC = AppController.getInstance();

//        doNothing().when(userBridge).deleteUser(any(User.class));
//        doNothing().when(clinicianBridge).deleteClinician(any(Clinician.class), anyString());
//        doNothing().when(administratorBridge).deleteAdmin(any(Administrator.class), anyString());
//
//        appC.setUserBridge(userBridge);
//        appC.setClinicianBridge(clinicianBridge);
//        appC.setAdministratorBridge(administratorBridge);
//
//        activeAdmins = appC.getAdmins();
//        activeClinicians = appC.getClinicians();
//        activeUsers = appC.getUsers();
//
//        activeAdmins.remove(testAdmin);
//        activeClinicians.remove(testClinician);
//        activeUsers.remove(testUser);
//
//        activeAdmins.add(testAdmin);
//        activeClinicians.add(testClinician);
//        activeUsers.add(testUser);
    }

    @After
    public void tearDown() {
//        activeAdmins.remove(testAdmin);
//        activeClinicians.remove(testClinician);
//        activeUsers.remove(testUser);
//
//
//        testAdmin.setDeleted(false);
//        testClinician.setDeleted(false);
//        testUser.setDeleted(false);

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

        System.out.println(testAdmin.isDeleted());
        Collection<Administrator> activeAdmins = appC.getAdmins();
        System.out.println(testAdmin.isDeleted());
        activeAdmins.remove(testAdmin);
        System.out.println(testAdmin.isDeleted());
        activeAdmins.add(testAdmin);
        System.out.println(testAdmin.isDeleted());

        appC.deleteAdmin(testAdmin);
        System.out.println(testAdmin.isDeleted());
        Assert.assertTrue(testAdmin.isDeleted());
        System.out.println(testAdmin.isDeleted());
    }
}
