package odms.controller;

import odms.commons.model.Administrator;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.NotFoundException;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;

@OdmsController
public class AdminController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;

    public AdminController(DBManager manager) throws SQLException {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        if (!handler.getExists(driver.getConnection(), Administrator.class, "default")) {
            System.out.println("added new clinician");
            Administrator administrator = new Administrator("default", "default", "", "", "admin");
            administrator.setDateLastModified(LocalDateTime.now());
            administrator.setDateCreated(LocalDateTime.now());
            handler.saveAdministrator(administrator, driver.getConnection());
        }


    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/admins")
    public Collection<Administrator> getAdministrator(@RequestParam("startIndex") int startIndex,
                                                      @RequestParam("count") int count,
                                                      @RequestParam(value = "q", required = false) String name) {
        try (Connection connection = driver.getConnection()) {
            System.out.println(startIndex);
            System.out.println(count);
            System.out.println(name);
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
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (SQLException ex) {
            Log.severe("cannot put administrator", ex);
            throw new ServerDBException(ex);
        }
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
        } catch (SQLException ex) {
            Log.severe("cannot put administrator " + username, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.DELETE, value = "/admins/{username}")
    public ResponseEntity deleteAdministrator(@PathVariable("username") String username) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteAdministrator(connection, username);
        } catch (SQLException ex) {
            Log.severe("cannot delete administrator", ex);
            throw new ServerDBException(ex);
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
