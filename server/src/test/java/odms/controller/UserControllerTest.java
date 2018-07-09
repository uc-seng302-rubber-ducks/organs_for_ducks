package odms.controller;

import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
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
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
}
