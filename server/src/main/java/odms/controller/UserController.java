package odms.controller;

import odms.commons.model.User;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.model.exception.ServerDBException;
import odms.model.response.UserOverview;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@OdmsController
public class UserController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;

    public UserController(DBManager manager) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
    }

    /**
     * sends a list of users depending on query parameters
     *
     * @return list of users
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users")
    public Collection<UserOverview> getUsers(@RequestParam("startIndex") int startIndex,
                                             @RequestParam("count") int count,
                                             @RequestParam(value = "name", required = false) Optional<String> name,
                                             @RequestParam(value = "region", required = false) Optional<String> region) {
        try (Connection connection = driver.getConnection()) {
//            TODO unable to implement yet. need handler.getUsersInRange(start, count) 5/6
//            if(name.isPresent()) {
//
//            }
            return null;
        } catch (SQLException ex) {
            Log.warning("cannot load all user data from db", ex);
            throw new ServerDBException(ex);
        }

    }


    @RequestMapping(method = RequestMethod.POST, value = "/users")
    public ResponseEntity postUser(@RequestBody User newUser) {
        try (Connection connection = driver.getConnection()) {
            List<User> users = new ArrayList<>();
            users.add(newUser);
            handler.saveUsers(users, connection);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (SQLException ex) {
            Log.severe("cannot add new user to db", ex);
            throw new ServerDBException(ex);
        }
    }
}
