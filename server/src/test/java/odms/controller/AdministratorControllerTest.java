package odms.controller;

import odms.commons.model.Administrator;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdministratorControllerTest {
    private AdminController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private SocketHandler socketHandler;
    private DBHandler handler;
    private Administrator testAdministrator;

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
        controller = new AdminController(manager, socketHandler);
        testAdministrator = new Administrator("12", "Steve", "", "", "");
    }

    @Test
    public void getAdministratorsShouldReturnSingleAdministratorOverview() throws SQLException {
        //set up data
        Collection<Administrator> administrators = new ArrayList<>();
        administrators.add(testAdministrator);
        when(handler.loadAdmins(any(Connection.class),anyInt(),anyInt(),anyString())).thenReturn(administrators);
        List<Administrator> results = new ArrayList<>(controller.getAdministrator(0, 1, ""));

        Assert.assertEquals(testAdministrator, results.get(0));
        Assert.assertEquals(results.size(), 1);
    }

    @Test(expected = ServerDBException.class)
    public void getAdministratorsShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());

        //should throw an exception here
        controller.getAdministrator("12");
    }


    @Test
    public void postAdministratorShouldReturnAcceptedIfConnectionValid() throws SQLException {
        //this is pretty dumb but any real error handling should be done within the DBHandler
        ResponseEntity res = controller.postAdministrator(testAdministrator);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    @Test(expected = ServerDBException.class)
    public void postAdministratorShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postAdministrator(testAdministrator);

    }

    @Test(expected = ServerDBException.class)
    public void getAdministratorShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getAdministrator("12");
    }

    @Test(expected = NotFoundException.class)
    public void getAdministratorShouldReturnNotFoundWhenNoClinicianFound() throws SQLException {
        when(handler.getOneAdministrator(any(Connection.class), anyString())).thenReturn(null);
        controller.getAdministrator("12");
    }

    @Test
    public void getAdministratorShouldReturnAdministratorIfExists() throws SQLException {
        when(handler.getOneAdministrator(any(Connection.class), anyString())).thenReturn(testAdministrator);
        Assert.assertEquals(testAdministrator, controller.getAdministrator("12"));
    }

    @Test(expected = ServerDBException.class)
    public void putAdministratorShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.putAdministrator("12", testAdministrator);
    }

    @Test
    public void putAdministratorShouldReturnOK() throws SQLException {
        ResponseEntity res = controller.putAdministrator("12", testAdministrator);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void deleteAdministratorShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteAdministrator("12");
    }

    @Test
    public void deleteAdministratorShouldReturnOK() throws SQLException {
        ResponseEntity res = controller.deleteAdministrator("12");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }
}
