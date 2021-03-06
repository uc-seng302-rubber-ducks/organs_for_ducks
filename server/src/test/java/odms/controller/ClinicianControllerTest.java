package odms.controller;

import odms.commons.model.Clinician;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.NotFoundException;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClinicianControllerTest {

    private ClinicianController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private SocketHandler socketHandler;
    private Clinician testClinician;

    @Before
    public void setUp() throws SQLException {
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        socketHandler = mock(SocketHandler.class);

        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new ClinicianController(manager, socketHandler);
        testClinician = new Clinician("steve", "12", "password");
    }

    @Test
    public void getCliniciansShouldReturnSingleClinicianOverview() throws SQLException {
        //set up data
        Collection<Clinician> clinicians = new ArrayList<>();
        clinicians.add(testClinician);
        when(handler.loadClinicians(any(Connection.class), anyInt(), anyInt(), anyString(), anyString())).thenReturn(clinicians);
        List<Clinician> results = new ArrayList<>(controller.getClinicians(0, 1, "", ""));

        Assert.assertEquals(testClinician, results.get(0));
        Assert.assertEquals(1, results.size());
    }

    @Test(expected = ServerDBException.class)
    public void getCliniciansShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());

        //should throw an exception here
        controller.getClinicians(0, 1, "", "");
    }

    @Test
    public void testGetBasicClinicians() throws SQLException {
        ComboBoxClinician testComboBoxClinician = new ComboBoxClinician("steven", "13");

        List<ComboBoxClinician> clinicians = new ArrayList<>();
        clinicians.add(testComboBoxClinician);
        when(handler.getBasicClinicians(any(Connection.class), anyString())).thenReturn(clinicians);
        List<ComboBoxClinician> results = new ArrayList<>(controller.getBasicClinicians(""));

        Assert.assertEquals(testComboBoxClinician, results.get(0));
        Assert.assertEquals(1, results.size());
    }

    @Test(expected = ServerDBException.class)
    public void getBasicCliniciansShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());

        //should throw an exception here
        controller.getBasicClinicians("");
    }


    @Test
    public void postClinicianShouldReturnAcceptedIfConnectionValid() throws SQLException {
        //this is pretty dumb but any real error handling should be done within the DBHandler
        ResponseEntity res = controller.postClinician(testClinician);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    @Test(expected = ServerDBException.class)
    public void postClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postClinician(testClinician);

    }

    @Test(expected = ServerDBException.class)
    public void getClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getClinician("12");
    }

    @Test(expected = NotFoundException.class)
    public void getClinicianShouldReturnNotFoundWhenNoClinicianFound() throws SQLException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(null);
        controller.getClinician("12");
    }

    @Test
    public void getClinicianShouldReturnClinicianIfExists() throws SQLException {
        when(handler.getOneClinician(any(Connection.class), anyString())).thenReturn(testClinician);
        Assert.assertEquals(testClinician, controller.getClinician("12"));
    }

    @Test(expected = ServerDBException.class)
    public void putClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.putClinician("12", testClinician);
    }

    @Test
    public void putClinicianShouldReturnOK() {
        ResponseEntity res = controller.putClinician("12", testClinician);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void deleteClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteClinician("12");
    }

    @Test
    public void deleteClinicianShouldReturnOK() {
        ResponseEntity res = controller.deleteClinician("12");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

}
