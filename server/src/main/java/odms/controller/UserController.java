package odms.controller;

import odms.commons.model.User;
import odms.commons.model._enum.EventTypes;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.NotFoundException;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

@OdmsController
public class UserController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;
    private SocketHandler socketHandler;

    public UserController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
        this.socketHandler = socketHandler;
    }

    /**
     * sends a list of users depending on query parameters
     *
     * @return list of users
     */
    @IsClinician
    @RequestMapping(
            method = RequestMethod.GET,
            value = "/users")
    public Collection<UserOverview> getUsers(@RequestParam("startIndex") int startIndex,
                                             @RequestParam("count") int count,
                                             @RequestParam(value = "name", required = false) String name,
                                             @RequestParam(value = "region", required = false) String region,
                                             @RequestParam(value = "gender", required = false) String gender) {
        try (Connection connection = driver.getConnection()) {
            Collection<User> rawUsers = handler.getUsers(connection, count, startIndex, name, region, gender.equals("All") ? "" : gender);
            Log.info("Getting all user overviews...");
            //converts each user in the collection to a userOverview and returns it
            return rawUsers.stream().map(UserOverview::fromUser).collect(Collectors.toList());
        } catch (SQLException ex) {
            Log.warning("cannot load all user data from db", ex);
            throw new ServerDBException(ex);
        } finally {
            Log.info("Finished");
        }

    }


    @RequestMapping(method = RequestMethod.POST, value = "/users")
    public ResponseEntity postUser(@RequestBody User newUser) {
        try (Connection connection = driver.getConnection()) {
            handler.saveUser(newUser, connection);
            socketHandler.broadcast(EventTypes.USER_UPDATE, newUser.getNhi(),newUser.getNhi());

        } catch (SQLException ex) {
            Log.severe("cannot add new user to db", ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update when posting user", ex);
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/{nhi}")
    public User getUser(@PathVariable("nhi") String nhi) {
        try (Connection connection = driver.getConnection()) {
            User result = handler.getOneUser(connection, nhi);
            if (result != null) {
                return result;
            }
            Log.warning("user not found with nhi " + nhi);
            throw new NotFoundException();

        } catch (SQLException ex) {
            Log.severe("cannot get user", ex);
            throw new ServerDBException(ex);
        }
    }


    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}")
    public ResponseEntity putUser(@PathVariable("nhi") String nhi, @RequestBody User user) {
        try (Connection connection = driver.getConnection()) {
            handler.updateUser(connection, nhi, user);
            socketHandler.broadcast(EventTypes.USER_UPDATE,user.getNhi(),nhi);
        } catch (SQLException ex) {
            Log.severe("cannot put user " + nhi, ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update when putting user", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/users/{nhi}")
    public ResponseEntity deleteUser(@PathVariable("nhi") String nhi) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteUser(connection, nhi);
            socketHandler.broadcast(EventTypes.USER_UPDATE, nhi, nhi);
        } catch (SQLException ex) {
            Log.severe("cannot delete user " + nhi, ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update when deleting user", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/users/exists/{nhi}")
    public boolean getExists(@PathVariable("nhi") String nhi) {
        try (Connection connection = driver.getConnection()) {
            return handler.getExists(connection, User.class, nhi);
        } catch (SQLException ex) {
            Log.severe("cannot find whether user exists", ex);
            throw  new ServerDBException(ex);
        }
    }
}
