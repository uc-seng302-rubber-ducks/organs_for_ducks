package odms.controller;

import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.exception.NotFoundException;
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
import java.util.ArrayList;
import java.util.Arrays;
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
    private User testUser;

    @Before
    public void setUp() throws SQLException{
        connection = mock(Connection.class);
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        driver = mock(JDBCDriver.class);
        when(driver.getConnection()).thenReturn(connection);
        when(manager.getHandler()).thenReturn(handler);
        when(manager.getDriver()).thenReturn(driver);
        controller = new UserController(manager);
        testUser = new User("steve", LocalDate.now(), "ABC1234");
    }

    @Test
    public void getUsersShouldReturnSingleUserOverview() throws SQLException{
        //set up data
        Collection<User> users = new ArrayList<>();
        users.add(testUser);
        UserOverview expected = UserOverview.fromUser(testUser);
        when(handler.getUsers(any(Connection.class), anyInt(), anyInt())).thenReturn(users);
        List<UserOverview> results = new ArrayList<>(controller.getUsers(0, 1, null, null));

        Assert.assertEquals(expected, results.get(0));
        Assert.assertEquals(results.size(), 1);
    }

    @Test(expected = ServerDBException.class)
    public void getUsersShouldThrowExceptionWhenNoConnection() throws SQLException{
        when(driver.getConnection()).thenThrow(new SQLException());

        //should throw an exception here
        controller.getUsers(0, 1, null, null);
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
    public void deleteUserShouldThrowExceptionWhenNoConnection() throws SQLException{
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.deleteUser("ABC1234");
    }

    @Test
    public void deleteUserShouldReturnOK() {
        ResponseEntity res = controller.deleteUser("ABC1234");
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void postProcedureShouldReturnCreated() throws SQLException {
        when(handler.getOneUser(any(Connection.class), anyString())).thenReturn(testUser);
        ResponseEntity res = controller.postMedicalProcedure("ABC1234", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "Transplantation of lung", new ArrayList<>()));
        Assert.assertEquals(HttpStatus.CREATED, res.getStatusCode());
    }

    @Test
    public void postProcedureWithNoUserShouldReturnNotFound() {
        ResponseEntity res = controller.postMedicalProcedure("ANC1111", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "moving of lungs", new ArrayList<>()));
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void postProcedureShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        controller.postMedicalProcedure("ABC1234", new MedicalProcedure(LocalDate.of(2017, 12, 3), "Lung Transplant", "moving of lungs", new ArrayList<>()));
    }

    @Test
    public void putProceduresShouldReturnOK() throws SQLException {
        when(handler.getOneUser(any(Connection.class), "ABC1234")).thenReturn(testUser);
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        ResponseEntity res = controller.putProcedure("ABC1234", procedures);
        Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    public void putProceduresShouldReturnNotFoundWhenNoUser() throws SQLException {
        when(handler.getOneUser(any(Connection.class), "ABC1234")).thenReturn(testUser);
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        ResponseEntity res = controller.putProcedure("ABC1111", procedures);
        Assert.assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test(expected = ServerDBException.class)
    public void putProceduresShouldThrowExceptionWhenNoConnection() throws SQLException {
        when(driver.getConnection()).thenThrow(new SQLException());
        List<MedicalProcedure> procedures = new ArrayList<>(Arrays.asList(new MedicalProcedure(LocalDate.now(), "test procedure", "tester", new ArrayList<>()),
                new MedicalProcedure(LocalDate.of(2018, 2, 25), "second test", "experimenting", new ArrayList<>())));
        controller.putProcedure("ABC1234", procedures);
    }


}
