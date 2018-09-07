package odms.commons.model;

import odms.commons.database.DBHandler;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.Address;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.commons.model.datamodel.DeathDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import test_utils.DBHandlerMocker;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private static final String PHOTO_TEST_FILE_PATH = "../server/src/test/resources/images/duck_jpg.jpg";

    @Before
    public void beforeTest() throws SQLException {
        testUser.setDateOfBirth(LocalDate.of(2000,1,1));
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
        verify(mockStmt, times(18)).executeUpdate();
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
        verify(mockStmt, times(11)).executeUpdate();
    }

    @Test
    public void testUpdateUserProfilePicture() throws SQLException, IOException {
        InputStream inputStream = new FileInputStream(PHOTO_TEST_FILE_PATH);

        dbHandler.updateProfilePhoto(User.class, testUser.getNhi(), inputStream, "image/jpg", connection);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test
    public void getUserProfilePicture() throws SQLException{
        when(mockResultSet.next()).thenReturn(true, false);
        dbHandler.getProfilePhoto(User.class, testUser.getNhi(), connection);
        verify(mockStmt, times(1)).executeQuery();
    }

    @Test
    public void testAddUserDonatingOrgans() throws SQLException {
        testUser.getDonorDetails().addOrgan(Organs.LUNG, null);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(13)).executeUpdate();
    }

    @Test
    public void testAddUserReceivingOrgan() throws SQLException {
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CONNECTIVE_TISSUE);
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CORNEA);
        testUser.getReceiverDetails().stopWaitingForAllOrgans();

        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(15)).executeUpdate();
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
        verify(mockStmt, times(13)).executeUpdate();
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
        verify(mockStmt, times(13)).executeUpdate();
        verify(mockResultSet, times(1)).getInt("medicationInstanceId");
        verify(mockStmt, times(2)).setString(2, "panadol");
        verify(mockStmt, times(1)).setNull(3, Types.TIMESTAMP);
    }

    @Test
    public void testAddClinician() throws SQLException {
        testClinician.addChange(new Change("Created clinician"));
        Collection<Clinician> clinicians = new ArrayList<>(Collections.singleton(testClinician));

        dbHandler.saveClinicians(clinicians, connection);
        verify(mockStmt, times(3)).executeUpdate();
    }

    @Test
    public void testAddAdmin() throws SQLException {
        testAdmin.addChange(new Change("Created administrator"));
        Collection<Administrator> admins = new ArrayList<>(Collections.singleton(testAdmin));

        dbHandler.saveAdministrators(admins, connection);
        verify(mockStmt, times(2)).executeUpdate();
    }

    @Test
    public void testUpdateClinicianProfilePicture() throws SQLException, FileNotFoundException {
        InputStream inputStream = new FileInputStream(PHOTO_TEST_FILE_PATH);

        dbHandler.updateProfilePhoto(Clinician.class, testClinician.getStaffId(), inputStream, "image/jpg", connection);
        verify(mockStmt, times(1)).executeUpdate();
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testRoleNotSupportUpdateProfilePicture() throws SQLException, FileNotFoundException {
        InputStream inputStream = new FileInputStream(PHOTO_TEST_FILE_PATH);

        dbHandler.updateProfilePhoto(Administrator.class, testAdmin.getUserName(), inputStream, "image/jpg", connection);
    }

    @Test
    public void testGetClinicianProfilePicture() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        dbHandler.getProfilePhoto(Clinician.class, testClinician.getStaffId(), connection);
        verify(mockStmt, times(1)).executeQuery();
    }

    @Test (expected = UnsupportedOperationException.class)
    public void testRoleNotSupportGetProfilePicture() throws SQLException {
        dbHandler.getProfilePhoto(Administrator.class, testAdmin.getUserName(), connection);
    }

    @Test
    public void testGetTransplantList() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        DBHandlerMocker.setTransplantResultSet(mockResultSet);
        when(mockResultSet.getString(eq("organName"))).thenReturn("LIVER");
        when(mockResultSet.getTimestamp("dob")).thenReturn(java.sql.Timestamp.valueOf(LocalDateTime.of(1,1,1,1,1)));
        when(mockResultSet.getDate(eq("dateRegistered"))).thenReturn(Date.valueOf(LocalDate.now()));
        dbHandler.getTransplantDetails(connection,0, 1, "", "", new String[] {});
        verify(mockStmt, times(1)).executeQuery();
    }

    @Test
    public void testGetDeathDetails() throws  SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        DBHandlerMocker.setDeathDetailsResultSet(mockResultSet);
        dbHandler.getDeathDetails(testUser, connection);
        verify(mockStmt, times(1)).executeQuery();
    }


    @Test
    public void testAddDeathDetails() throws SQLException {
        Address address = new Address("", "", "", "Christchurch", "Canterbury", "", "New Zealand");
        DeathDetails deathDetails = new DeathDetails(LocalDateTime.of(2010, 1, 1, 2, 45), address);
        testUser.setDeathDetails(deathDetails);

        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(11)).executeUpdate();

    }

    @Test
    public void testAddNullDeathDetails() throws SQLException {
        Address address = new Address("", "", "", "", "", "", "");
        DeathDetails deathDetails = new DeathDetails(null, address);
        testUser.setDeathDetails(deathDetails);

        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        verify(mockStmt, times(11)).executeUpdate();
    }

    @Test
    public void testGetAppointmentId() throws SQLException {
        LocalDateTime testDate = LocalDateTime.now().plusDays(2);
        Appointment testAppointment = new Appointment("JEF1234", "id1234", AppointmentCategory.GENERAL_CHECK_UP, testDate, "Help", AppointmentStatus.PENDING);

        when(mockResultSet.getInt("apptId")).thenReturn(0);
        int id = dbHandler.getAppointmentId(connection, testAppointment);
        verify(mockStmt, times(1)).executeQuery();
        Assert.assertEquals(0, id);

    }

    @Test
    public void testGetBasicClinicians() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        testClinician.setMiddleName("mid");
        testClinician.setLastName("last");
        DBHandlerMocker.setClinicianResultSet(mockResultSet, testClinician);
        Collection<ComboBoxClinician> clinicians = dbHandler.getBasicClinicians(connection,"");

        verify(mockStmt, times(1)).executeQuery();
        Assert.assertEquals("Jon mid last", clinicians.iterator().next().toString());
    }

    @Test
    public void testDeleteAppointment() throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1);
        dbHandler.deleteAppointment(appointment, connection);
        verify(mockStmt, times(1)).executeUpdate();
    }

}
