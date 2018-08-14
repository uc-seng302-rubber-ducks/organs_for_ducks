package odms.model;

import odms.commons.exception.ProfileAlreadyExistsException;
import odms.commons.exception.ProfileNotFoundException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.controller.AppController;
import odms.bridge.AdministratorBridge;
import odms.bridge.ClinicianBridge;
import odms.bridge.UserBridge;
import org.junit.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DeleteRestoreProfileTest {

    private static User testUser;
    private static Clinician testClinician;
    private static Administrator testAdmin;
    private static AppController appC;
    private static UserBridge userBridge = mock(UserBridge.class);
    private static ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private static AdministratorBridge administratorBridge = mock(AdministratorBridge.class);
    private static Collection<Administrator> activeAdmins;
    private static List<Clinician> activeClinicians;
    private static List<User> activeUsers;


    @BeforeClass
    public static void setUpClass() {
        testUser = new User("Stan", LocalDate.of(2000, 3, 5), "ABC4321");
        testClinician = new Clinician("Bob", "id",  "1234");
        testAdmin = new Administrator("nameuser", "first", "middle", "last", "1234");

        appC = AppController.getInstance();

        doNothing().when(userBridge).deleteUser(any(User.class));
        doNothing().when(clinicianBridge).deleteClinician(any(Clinician.class), anyString());
        doNothing().when(administratorBridge).deleteAdmin(any(Administrator.class), anyString());

        appC.setUserBridge(userBridge);
        appC.setClinicianBridge(clinicianBridge);
        appC.setAdministratorBridge(administratorBridge);

        activeAdmins = appC.getAdmins();
        activeClinicians = appC.getClinicians();
        activeUsers = appC.getUsers();

        activeAdmins.remove(testAdmin);
        activeClinicians.remove(testClinician);
        activeUsers.remove(testUser);
    }

    @Before
    public void setUp() {
        activeAdmins.add(testAdmin);
        activeClinicians.add(testClinician);
        activeUsers.add(testUser);
    }

    @After
    public void tearDown() {
        activeAdmins.remove(testAdmin);
        activeClinicians.remove(testClinician);
        activeUsers.remove(testUser);


        testAdmin.setDeleted(false);
        testClinician.setDeleted(false);
        testUser.setDeleted(false);
    }

    @Test
    public void testDeleteUser() {
        appC.deleteUser(testUser);
        Assert.assertTrue(testUser.isDeleted());
    }

    @Test
    public void testDeleteClinician() {
        appC.deleteClinician(testClinician);
        Assert.assertTrue(testClinician.isDeleted());
    }

    @Test
    public void testDeleteAdmin() {
        appC.deleteAdmin(testAdmin);
        Assert.assertTrue(testAdmin.isDeleted());
    }
}
