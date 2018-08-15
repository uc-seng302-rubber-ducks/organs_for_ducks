package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.Administrator;
import odms.commons.model._enum.EventTypes;
import odms.commons.utils.Log;
import odms.exception.NotFoundException;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

@OdmsController
public class AdminController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;
    private SocketHandler socketHandler;

    public AdminController(DBManager manager, SocketHandler socketHandler) throws SQLException {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        this.socketHandler = socketHandler;
        if (!handler.getExists(driver.getConnection(), Administrator.class, "default")) {
            Administrator administrator = new Administrator("default", "default", "", "", "admin");
            handler.saveAdministrator(administrator, driver.getConnection());
        }


    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/admins")
    public Collection<Administrator> getAdministrator(@RequestParam("startIndex") int startIndex,
                                                      @RequestParam("count") int count,
                                                      @RequestParam(value = "q", required = false) String name) {
        try (Connection connection = driver.getConnection()) {
            return handler.loadAdmins(connection,startIndex, count,name);
        } catch (SQLException ex) {
            Log.severe("could not get admins", ex);
            throw new ServerDBException(ex);
        }
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.POST, value = "/admins")
    public ResponseEntity postAdministrator(@RequestBody Administrator newAdmin) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            handler.saveAdministrator(newAdmin, connection);
            socketHandler.broadcast(EventTypes.ADMIN_UPDATE, newAdmin.getUserName(), newAdmin.getUserName());
        } catch (SQLException ex) {
            Log.severe("cannot put administrator", ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update after posting admin", ex);
        }
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/admins/{username}")
    public Administrator getAdministrator(@PathVariable("username") String username) throws SQLException {
        try (Connection collection = driver.getConnection()) {
            Administrator result = handler.getOneAdministrator(collection, username);
            if (result != null) {
                return result;
            }
            Log.warning("administratoe not found with username " + username);
            throw new NotFoundException();
        } catch (SQLException ex) {
            Log.severe("cannot get administrator", ex);
            throw new ServerDBException(ex);
        }
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.PUT, value = "/admins/{username}")
    public ResponseEntity putAdministrator(@PathVariable("username") String username, @RequestBody Administrator administrator) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            handler.updateAdministrator(connection, username, administrator);
            socketHandler.broadcast(EventTypes.ADMIN_UPDATE, username, administrator.getUserName());
        } catch (SQLException ex) {
            Log.severe("cannot put administrator " + username, ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update after putting admin", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.DELETE, value = "/admins/{username}")
    public ResponseEntity deleteAdministrator(@PathVariable("username") String username) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteAdministrator(connection, username);
            socketHandler.broadcast(EventTypes.ADMIN_UPDATE, username, null);
        } catch (SQLException ex) {
            Log.severe("cannot delete administrator", ex);
            throw new ServerDBException(ex);
        } catch (IOException ex) {
            Log.warning("failed to broadcast update after deleting admin", ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/admins/exists/{username}")
    public boolean getExists(@PathVariable("username") String username) {
        try (Connection connection = driver.getConnection()) {
            return handler.getExists(connection, Administrator.class, username);
        } catch (SQLException ex) {
            Log.severe("cannot check whether admin exists", ex);
            throw new ServerDBException(ex);
        }
    }
}
