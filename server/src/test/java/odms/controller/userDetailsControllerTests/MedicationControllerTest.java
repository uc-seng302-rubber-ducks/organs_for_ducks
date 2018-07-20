package odms.controller.userDetailsControllerTests;

import odms.commons.model.User;
import odms.commons.model.datamodel.Medication;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.controller.user.details.MedicationController;
import odms.exception.ServerDBException;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MedicationControllerTest {

    private MedicationController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private User testUser;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new MedicationController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void postMedicationShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        ResponseEntity res = controller.postMedication("ABC1234", new Medication("panadol"));
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postMedicationWithNoUserShouldReturnNotFound() {
        ResponseEntity res = controller.postMedication("ANC1111", new Medication("panadol"));
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postMedicationShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postMedication("ABC1234", new Medication("panadol"));
    }

    @Test
    public void putMedicationsShouldReturnOK() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        List<Medication> medications = new ArrayList<>(Arrays.asList(new Medication("aspirin"),
                new Medication("panadol")));
        ResponseEntity res = controller.putMedications("ABC1234", medications);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void putMedicationsShouldReturnNotFoundWhenNoUser() {
        List<Medication> medications = new ArrayList<>(Arrays.asList(new Medication("aspirin"),
                new Medication("panadol")));
        ResponseEntity res = controller.putMedications("ABC1111", medications);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putMedicationsShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        List<Medication> medications = new ArrayList<>(Arrays.asList(new Medication("aspirin"),
                new Medication("panadol")));
        controller.putMedications("ABC1234", medications);
    }

    @Test
    public void postCurrentMedicationShouldAddToCorrectList() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Medication testMed = new Medication("panadol", new ArrayList<>(Collections.singleton(LocalDateTime.of(1980, 3, 12, 10, 6))));
        controller.postMedication(testUser.getNhi(), testMed);
        Assert.assertTrue(testUser.getCurrentMedication().contains(testMed));
    }

    @Test
    public void postMedicationShouldAddToCorrectList() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Medication testMed = new Medication("panadol", new ArrayList<>(Arrays.asList(LocalDateTime.of(1980, 3, 12, 10, 6),
                LocalDateTime.now().minusDays(5))));
        Medication testMed2 = new Medication("aspirin", new ArrayList<>(Collections.singleton(LocalDateTime.of(1980, 3, 12, 10, 6))));
        controller.putMedications(testUser.getNhi(), Arrays.asList(testMed, testMed2));
        Assert.assertTrue(testUser.getPreviousMedication().contains(testMed));
        Assert.assertTrue(testUser.getPreviousMedication().size() == 1);
        Assert.assertTrue(testUser.getCurrentMedication().contains(testMed2));
        Assert.assertTrue(testUser.getCurrentMedication().size() == 1);
    }

    @Test
    public void putMedicationsShouldAddToCorrectLists() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Medication testMed = new Medication("panadol", new ArrayList<>(Arrays.asList(LocalDateTime.of(1980, 3, 12, 10, 6),
                LocalDateTime.now().minusDays(5))));

        controller.postMedication(testUser.getNhi(), testMed);
        Assert.assertTrue(testUser.getPreviousMedication().contains(testMed));
    }



}
