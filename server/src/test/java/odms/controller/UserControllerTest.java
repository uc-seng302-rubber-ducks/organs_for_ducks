package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.User;
import odms.commons.model.datamodel.ComboBoxClinician;
import odms.commons.model.dto.UserOverview;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController controller;
    private Connection connection;
    private JDBCDriver driver;
    private DBManager manager;
    private DBHandler handler;
    private SocketHandler socketHandler;
    private User testUser;
    private ComboBoxClinician testComboBoxClinician;

    @Before
    public void setUp() throws SQLException{
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        socketHandler = mock(SocketHandler.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new UserController(manager, socketHandler);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
        testComboBoxClinician = new ComboBoxClinician("Tester", "0");
    }

    @Test
    public void getUsersShouldReturnSingleUserOverview() throws SQLException {
        //set up data
        Collection<User> users = new ArrayList<>();
        users.add(testUser);
        UserOverview expected = UserOverview.fromUser(testUser);
        when(handler.getUsers(any(Connection.class), anyInt(), anyInt(), anyString(), anyString(), anyString())).thenReturn(users);
        List<UserOverview> results = new ArrayList<>(controller.getUsers(0, 1, "", "", ""));

        Assert.assertEquals(expected, results.get(0));
        Assert.assertEquals(1, results.size());
    }

    @Test(expected = ServerDBException.class)
    public void getUsersShouldThrowExceptionWhenNoConnection() throws SQLException{
        when(driver.getConnection()).thenThrow(new SQLException());

        //should throw an exception here
        controller.getUsers(0, 1, "", "", "");
    }


    @Test
    public void postUserShouldReturnAcceptedIfConnectionValid() {
        //this is pretty dumb but any real error handling should be done within the DBHandler
        ResponseEntity res = controller.postUser(testUser);
        Assert.assertEquals(res.getStatusCode(), HttpStatus.ACCEPTED);
    }

    @Test(expected = ServerDBException.class)
    public void postUserShouldThrowExceptionWhenNoConnection() throws SQLException{
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postUser(testUser);

    }

    @Test(expected = ServerDBException.class)
    public void getUserShouldThrowExceptionWhenNoConnection() throws SQLException{
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getUser("ABC1234");
    }

    @Test(expected = NotFoundException.class)
    public void getUserShouldReturnNotFoundWhenNoUserFound() throws SQLException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(null);
        controller.getUser("ABC1234");
    }

    @Test
    public void getUserShouldReturnUserIfExists() throws  SQLException{
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        Assert.assertEquals(testUser, controller.getUser("ABC1234"));
    }

    @Test(expected = ServerDBException.class)
    public void putUserShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.putUser("ABC1234", testUser);
    }

    @Test
    public void putUserShouldReturnOK() {
        ResponseEntity res = controller.putUser("ABC1234", testUser);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void deleteUserShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteUser("ABC1234");
    }

    @Test
    public void deleteUserShouldReturnOK() {
        ResponseEntity res = controller.deleteUser("ABC1234");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void getPreferredClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.getPreferredClinician("ABC1234");
    }

    @Test
    public void getPreferredClinicianReturnOK() throws SQLException {
        when(handler.getPreferredBasicClinician(any(Connection.class), anyString())).thenReturn(testComboBoxClinician);
        Assert.assertEquals(testComboBoxClinician, controller.getPreferredClinician("ABC1234"));
    }

    @Test(expected = ServerDBException.class)
    public void putPreferredClinicianShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.putPreferredClinician("ABC1234", "0");
    }

    @Test
    public void putPreferredClinicianReturnOK() {
        ResponseEntity res = controller.putPreferredClinician("ABC1234", "0");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }


}
