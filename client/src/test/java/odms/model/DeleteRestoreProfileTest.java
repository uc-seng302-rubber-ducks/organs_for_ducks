package odms.model;

import odms.controller.AppController;
import odms.commons.exception.ProfileAlreadyExistsException;
import odms.commons.exception.ProfileNotFoundException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import org.junit.*;

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
        testClinician = new Clinician("Bob", "id",  "1234");
        testAdmin = new Administrator("nameuser", "first", "middle", "last", "1234");

        appC = AppController.getInstance();
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

    @Test
    public void testRestoreUserSuccessful() {
        appC.deleteUser(testUser);

        try {
            appC.undoDeletion(testUser);
        } catch (ProfileAlreadyExistsException | ProfileNotFoundException e) {
            fail();
        }

        Assert.assertFalse(testUser.isDeleted());
    }

    @Test
    public void testRestoreUserNotFoundException() {
        try {
            appC.undoDeletion(testUser);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertFalse(testUser.isDeleted());
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
            Assert.assertTrue(testUser.isDeleted());
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

        Assert.assertFalse(testClinician.isDeleted());
        Assert.assertTrue(activeClinicians.contains(testClinician));
    }


    @Test
    public void testRestoreClinicianNotFoundException() {
        try {
            appC.undoClinicianDeletion(testClinician);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertFalse(testClinician.isDeleted());
            Assert.assertTrue(activeClinicians.contains(testClinician));
        } catch (ProfileAlreadyExistsException e) {
            fail("ProfileNotFoundException should've been thrown");
        }
    }

    @Test
    public void testRestoreClinicianExistsException() {
        appC.deleteClinician(testClinician);
        Clinician newClinician = new Clinician("NotBob", "id", "1234");
        activeClinicians.add(newClinician);

        try {
            appC.undoClinicianDeletion(testClinician);
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileNotFoundException e) {
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileAlreadyExistsException e) {
            Assert.assertTrue(testClinician.isDeleted());
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

        Assert.assertFalse(testAdmin.isDeleted());
    }

    @Test
    public void testRestoreAdminNotFoundException() {
        try {
            appC.undoAdminDeletion(testAdmin);
            fail("ProfileNotFoundException should've been thrown");
        } catch (ProfileNotFoundException e) {
            Assert.assertFalse(testAdmin.isDeleted());
            Assert.assertTrue(activeAdmins.contains(testAdmin));
        } catch (ProfileAlreadyExistsException e) {
            fail("ProfileNotFoundException should've been thrown");
        }
    }

    @Test
    public void testRestoreAdminExistsException() {
        appC.deleteAdmin(testAdmin);
        Administrator newAdmin = new Administrator("nameuser", "Notfirst", "middle", "last", "1234");
        activeAdmins.add(newAdmin);

        try {
            appC.undoAdminDeletion(testAdmin);
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileNotFoundException e) {
            fail("ProfileAlreadyExistsException should've been thrown");
        } catch (ProfileAlreadyExistsException e) {
            Assert.assertTrue(testAdmin.isDeleted());
            Assert.assertTrue(activeAdmins.contains(newAdmin));
            Assert.assertTrue(testAdmin.equals(newAdmin));
        }
    }

}
