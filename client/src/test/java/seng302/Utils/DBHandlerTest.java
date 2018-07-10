package seng302.Utils;

import odms.commons.model.Change;
import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.DBHandler;
import org.junit.*;
import seng302.TestUtils.DBHandlerMocker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Ignore
public class DBHandlerTest {
    private DBHandler dbHandler;
    private Connection connection;
    private User expected;
    private User testUser = new User("Eiran", LocalDate.of(2018, 2, 20), "ABC1111");

    /**
     * Helper function to convert date string
     * to LocalDateTime object.
     * @param date date string from database
     * @return LocalDateTime object
     */
    private LocalDateTime dateToLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(date, formatter);
    }

    @Before
    public void beforeTest() {
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

    }

    @After
    public void afterTest() throws SQLException {
        connection.close();
    }


/*
    @Test
    public void testUserInstanceCreatedValid() throws SQLException {
        Collection<User> users = dbHandler.loadUsers(connection, 10, 0);
        System.out.println(users);
        Assert.assertEquals(3, users.size());
    }

    @Test
    public void testDecodeUserInstanceCreatedValid() throws SQLException {
        Collection<User> users = dbHandler.loadUsers(connection, 10, 0);
        User actual = users.iterator().next();
        Assert.assertTrue(actual.getNhi().equals(expected.getNhi()));
        Assert.assertTrue(actual.getMiddleName().equals(expected.getMiddleName()));
        Assert.assertTrue(actual.getLastName().equals(expected.getLastName()));
        Assert.assertTrue(actual.getTimeCreated().equals(expected.getTimeCreated()));
        //Assert.assertTrue (actual.getLastModified().equals(expected.getLastModified()));
//        System.out.println(actual.getLastModified());
//        System.out.println(expected.getLastModified());

    }*/

    @Test
    public void testAddNewUser() throws SQLException {
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(stmt);
        doNothing().when(stmt).setString(anyInt(), anyString());
        when(stmt.executeQuery()).thenReturn(resultSet);
        DBHandlerMocker.setUserResultSet(resultSet, testUser);
        testUser.addChange(new Change("Created")); // needs a new change otherwise will not be passed through to the DB
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        when(resultSet.next()).thenReturn(true, false);
        User returned = dbHandler.getOneUser(connection, testUser.getNhi());
        Assert.assertTrue(returned.getNhi().equals(testUser.getNhi()));
        Assert.assertTrue(returned.getFirstName().equals(testUser.getFirstName()));
        Assert.assertTrue(returned.getLastName().equals(testUser.getLastName()));
        Assert.assertTrue(returned.getDateOfBirth().equals(testUser.getDateOfBirth()));
    }

    @Test
    public void testDeleteUser() throws SQLException {
        testAddNewUser();
        testUser.setDeleted(true);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
    }

    @Test
    public void testUpdateUser() throws SQLException {
        testAddNewUser();
        testUser.setHeight(1.89);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
        testDeleteUser();
    }

    @Test
    public void testAddUserDonatingOrgans() throws SQLException {
        testAddNewUser();
        testUser.getDonorDetails().addOrgan(Organs.LUNG);
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
    }

    @Test
    public void testAddUserReceivingOrgan() throws SQLException {
        testAddNewUser();
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CONNECTIVE_TISSUE);
        testUser.getReceiverDetails().startWaitingForOrgan(Organs.CORNEA);
        testUser.getReceiverDetails().stopWaitingForAllOrgans();

        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
    }

    @Test
    public void testAddProcedureToUser() throws SQLException {
        testAddNewUser();
        ArrayList<Organs> organsAffected = new ArrayList<>();
        organsAffected.add(Organs.LUNG);
        testUser.addMedicalProcedure(new MedicalProcedure(LocalDate.of(2018, 5, 3), "Lung Transplant", "Lung dieded", organsAffected));
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
    }

    @Test
    public void testAddMedicationsToUser() throws SQLException {
        testAddNewUser();
        testUser.addCurrentMedication("panadol");
        Collection<User> users = new ArrayList<>();
        users.add(testUser);

        dbHandler.saveUsers(users, connection);
    }
}
