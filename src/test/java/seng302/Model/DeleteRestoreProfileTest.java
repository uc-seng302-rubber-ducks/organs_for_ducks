package seng302.Model;

import org.junit.*;
import seng302.Controller.AppController;
import seng302.Exception.ProfileAlreadyExistsException;
import seng302.Exception.ProfileNotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.fail;

public class DeleteRestoreProfileTest {

    private static User testUser;
    private static Clinician testClinician;
    private static Administrator testAdmin;
    private static AppController appC;
    private static Collection<Administrator> activeAdmins;
    private static List<Clinician> activeClinicians;
    private static List<User> activeUsers;


    @BeforeClass
    public static void setUpClass() {
        testUser = new User("Stan", LocalDate.of(2000, 3, 5), "ABC4321");
        testClinician = new Clinician("Bob", "id", "living space", "chch", "1234");
        testAdmin = new Administrator("nameuser","first","middle","last","1234");

        appC = AppController.getInstance();
        activeAdmins = appC.getAdmins();
        activeClinicians = appC.getClinicians();
        activeUsers = appC.getUsers();

        activeAdmins.clear();
        activeClinicians.clear();
        activeUsers.clear();
    }

    @Before
    public void setUp() {
        activeAdmins.add(testAdmin);
        activeClinicians.add(testClinician);
        activeUsers.add(testUser);
    }

    @After
    public void tearDown() {
        activeAdmins.clear();
        activeClinicians.clear();
        activeUsers.clear();



        appC.getDeletedUsers().clear();
        appC.getDeletedAdmins().clear();
        appC.getDeletedClinicians().clear();
    }

    @Test
    public void testDeleteUser() {
        appC.deleteUser(testUser);
        Assert.assertTrue(!activeUsers.contains(testUser));
        Assert.assertTrue(appC.getDeletedUsers().contains(testUser));
    }

    @Test
    public void testDeleteClinician() {
        appC.deleteClinician(testClinician);
        Assert.assertTrue(!activeClinicians.contains(testClinician));
        Assert.assertTrue(appC.getDeletedClinicians().contains(testClinician));
    }

    @Test
    public void testDeleteAdmin() {
        appC.deleteAdmin(testAdmin);
        Assert.assertTrue(!activeAdmins.contains(testAdmin));
        Assert.assertTrue(appC.getDeletedAdmins().contains(testAdmin));
    }

    @Test
    public void testRestoreUserSuccessful() {
        appC.deleteUser(testUser);

        try {
            appC.undoDeletion(testUser);
        } catch (ProfileAlreadyExistsException | ProfileNotFoundException e) {
            fail();
        }

        Assert.assertTrue(!appC.getDeletedUsers().contains(testUser));
        Assert.assertTrue(activeUsers.contains(testUser));
    }

    @Test
    public void testRestoreUserNotFoundException() {
        try {
            appC.undoDeletion(testUser);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertTrue(!appC.getDeletedUsers().contains(testUser));
            Assert.assertTrue(activeUsers.contains(testUser));
        } catch (ProfileAlreadyExistsException e) {
            fail("ProfileNotFoundException should've been thrown");
        }
    }

    @Test
    public void testRestoreUserExistsException() {
        appC.deleteUser(testUser);
        User newUser = new User("NotStan", LocalDate.of(2000, 3, 5), "ABC4321");
        activeUsers.add(newUser);

        try {
            appC.undoDeletion(testUser);
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileNotFoundException e) {
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileAlreadyExistsException e) {
            Assert.assertTrue(appC.getDeletedUsers().contains(testUser));
            Assert.assertTrue(activeUsers.contains(newUser));
            Assert.assertTrue(testUser.equals(newUser));
        }
    }

    @Test
    public void testRestoreClinicianSuccessful() {
        appC.deleteClinician(testClinician);

        try {
            appC.undoClinicianDeletion(testClinician);
        } catch (ProfileAlreadyExistsException | ProfileNotFoundException e) {
            fail();
        }

        Assert.assertTrue(!appC.getDeletedClinicians().contains(testClinician));
        Assert.assertTrue(activeClinicians.contains(testClinician));
    }


    @Test
    public void testRestoreClinicianNotFoundException() {
        try {
            appC.undoClinicianDeletion(testClinician);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertTrue(!appC.getDeletedClinicians().contains(testClinician));
            Assert.assertTrue(activeClinicians.contains(testClinician));
        } catch (ProfileAlreadyExistsException e) {
            fail("ProfileNotFoundException should've been thrown");
        }
    }

    @Test
    public void testRestoreClinicianExistsException() {
        appC.deleteClinician(testClinician);
        Clinician newClinician = new Clinician("NotBob", "id", "living space", "chch", "1234");
        activeClinicians.add(newClinician);

        try {
            appC.undoClinicianDeletion(testClinician);
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileNotFoundException e) {
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileAlreadyExistsException e) {
            Assert.assertTrue(appC.getDeletedClinicians().contains(testClinician));
            Assert.assertTrue(activeClinicians.contains(newClinician));
            Assert.assertTrue(testClinician.equals(newClinician));
        }
    }


    @Test
    public void testRestoreAdminSuccessful() {
        appC.deleteAdmin(testAdmin);

        try {
            appC.undoAdminDeletion(testAdmin);
        } catch (ProfileAlreadyExistsException | ProfileNotFoundException e) {
            fail();
        }

        Assert.assertTrue(!appC.getDeletedAdmins().contains(testAdmin));
        Assert.assertTrue(activeAdmins.contains(testAdmin));
    }

    @Test
    public void testRestoreAdminNotFoundException() {
        try {
            appC.undoAdminDeletion(testAdmin);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertTrue(!appC.getDeletedAdmins().contains(testAdmin));
            Assert.assertTrue(activeAdmins.contains(testAdmin));
        } catch (ProfileAlreadyExistsException e) {
            fail("ProfileNotFoundException should've been thrown");
        }
    }

    @Test
    public void testRestoreAdminExistsException() {
        appC.deleteAdmin(testAdmin);
        Administrator newAdmin = new Administrator("nameuser","Notfirst","middle","last","1234");
        activeAdmins.add(newAdmin);

        try {
            appC.undoAdminDeletion(testAdmin);
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileNotFoundException e) {
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileAlreadyExistsException e) {
            Assert.assertTrue(appC.getDeletedAdmins().contains(testAdmin));
            Assert.assertTrue(activeAdmins.contains(newAdmin));
            Assert.assertTrue(testAdmin.equals(newAdmin));
        }
    }

}
