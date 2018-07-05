package odms.controller;

import odms.commons.model.User;
import odms.commons.model.transfer.UserOverview;
import odms.commons.utils.DBHandler;
import odms.utils.DBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController controller;
    private DBManager manager;
    private DBHandler handler;

    @Before
    public void setUp() {
        manager = mock(DBManager.class);
        handler = mock(DBHandler.class);
        when(manager.getHandler()).thenReturn(handler);

        controller = new UserController(manager);
    }

    @Test
    public void getUsersShouldReturnSingleUserOverview() throws SQLException{
        //set up data
        User testUser = new User("steve", LocalDate.now(), "ABC1234");
        Collection<User> users = new ArrayList<>();
        users.add(testUser);
        UserOverview expected = UserOverview.fromUser(testUser);
        when(handler.getUsers(any(Connection.class), anyInt(), anyInt())).thenReturn(users);

        List<UserOverview> results = new ArrayList<>(controller.getUsers(0, 1, null, null));

        Assert.assertEquals(expected, results.get(0));
        Assert.assertEquals(results.size(), 1);
    }
}
