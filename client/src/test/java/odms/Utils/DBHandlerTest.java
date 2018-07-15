package odms.Utils;

import odms.commons.model.*;
import odms.commons.model._enum.Organs;
import odms.commons.utils.DBHandler;
import org.junit.*;
import seng302.TestUtils.DBHandlerMocker;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DBHandlerTest {
    private DBHandler dbHandler;
    private Connection connection;
    private PreparedStatement mockStmt;
    private ResultSet mockResultSet;
    private User testUser = new User("Eiran", LocalDate.of(2018, 2, 20), "ABC1111");
    private Clinician testClinician = new Clinician("Jon", "16", "password");
    private Administrator testAdmin = new Administrator("username", "James", "", "", "admin");

    @Before
    public void beforeTest() throws SQLException {
        odms.TestUtils.SQLScriptRunner.run();
        expected = new User();
        expected.setNhi("ABC1234");
        expected.setFirstName("Allan");
        expected.setMiddleName("Danny Zurich");
        expected.setLastName("Levi");
        expected.setPreferredFirstName("Al");
        expected.setTimeCreated(dateToLocalDateTime("1997-01-01 00:01:01.0"));
        expected.setLastModified(dateToLocalDateTime("1997-05-01 13:01:01.0"));
        //TODO: set expected's profile picture here
        expected.setGenderIdentity("Male");
        expected.setBirthGender("Male");
        expected.setSmoker(true);
        expected.setAlcoholConsumption("High");
        expected.setHeight(163.7);
        expected.setWeight(65.8);
        expected.setHomePhone(null);
        expected.setCellPhone("0221453566");
        expected.setEmail("aaronB@gmail.com");

        dbHandler = new DBHandler();
        connection = mock(Connection.class);

        mockStmt = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        when(mockResultSet.next()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(mockStmt);
        doNothing().when(mockStmt).setString(anyInt(), anyString());
        when(mockStmt.executeQuery()).thenReturn(mockResultSet);
        DBHandlerMocker.setUserResultSet(mockResultSet, testUser);
    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }


    @Test
    public void testDecodeUserInstanceCreatedValid() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        User returned = dbHandler.getOneUser(connection, testUser.getNhi());
        Assert.assertTrue(returned.getNhi().equals(testUser.getNhi()));
        Assert.assertTrue(returned.getFirstName().equals(testUser.getFirstName()));
        Assert.assertTrue(returned.getLastName().equals(testUser.getLastName()));
        Assert.assertTrue(returned.getDateOfBirth().equals(testUser.getDateOfBirth()));
    }

    @Test
    public void testAddNewUser() throws SQLException {
        testUser.addChange(new Change("Created")); // needs a new change otherwise will not be passed through to the DB
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        when(mockResultSet.next()).thenReturn(false).thenReturn(true);
        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(17)).executeUpdate();
    }

    @Test
    public void testDeleteUser() throws SQLException {
        testUser.setDeleted(true);
        when(mockResultSet.next()).thenReturn(true);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateUser() throws SQLException {

        testUser.setHeight(1.89);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(10)).executeUpdate();
    }

    @Test
    public void testAddUserDonatingOrgans() throws SQLException {
        testUser.getDonorDetails().addOrgan(Organs.LUNG);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(11)).executeUpdate();
    }

    @Test
    public void testAddUserReceivingOrgan() throws SQLException {
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CONNECTIVE_TISSUE);
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CORNEA);
        testUser.getReceiverDetails().stopWaitingForAllOrgans();

        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(14)).executeUpdate();
        verify(mockStmt, never()).setNull(3, Types.DATE);
        verify(mockStmt, times(2)).setInt(2, Organs.CONNECTIVE_TISSUE.getDbValue());
        verify(mockStmt, times(2)).setInt(2, Organs.CORNEA.getDbValue());
    }

    @Test
    public void testAddProcedureToUser() throws SQLException {
        ArrayList<Organs> organsAffected = new ArrayList<>();
        organsAffected.add(Organs.LUNG);
        MedicalProcedure procedure = new MedicalProcedure(LocalDate.of(2018, 5, 3), "Lung Transplant", "Lung dieded", organsAffected);
        testUser.addMedicalProcedure(procedure);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(12)).executeUpdate();
        verify(mockResultSet, times(1)).getInt("procedureId");
        verify(mockStmt, times(2)).setString(2, procedure.getSummary());
        verify(mockStmt, times(1)).setInt(1, Organs.LUNG.getDbValue());
    }

    @Test
    public void testAddMedicationsToUser() throws SQLException {
        testUser.addCurrentMedication("panadol");
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(12)).executeUpdate();
        verify(mockResultSet, times(1)).getInt("medicationInstanceId");
        verify(mockStmt, times(2)).setString(2, "panadol");
        verify(mockStmt, times(1)).setNull(3, Types.TIMESTAMP);
    }

    @Test
    @Ignore //TODO: Unignore when changes have been properly made.
    public void testAddClinician() throws SQLException {
        testClinician.addChange(new Change("Created clinician"));
        Collection<Clinician> clinicians = new ArrayList<>(Collections.singleton(testClinician));

        dbHandler.saveClinicians(clinicians, connection);
        verify(mockStmt, times(4)).executeUpdate();
    }

    @Test
    @Ignore //TODO: Unignore when changes have been properly made.
    public void testAddAdmin() throws SQLException {
        testAdmin.addChange(new Change("Created administrator"));
        Collection<Administrator> admins = new ArrayList<>(Collections.singleton(testAdmin));

        dbHandler.saveAdministrators(admins, connection);
        verify(mockStmt, times(4)).executeUpdate();
    }
}
