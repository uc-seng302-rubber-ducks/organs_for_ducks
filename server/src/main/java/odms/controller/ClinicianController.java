package odms.controller;

import odms.commons.model.Clinician;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.NotFoundException;
import odms.exception.ServerDBException;
import odms.security.IsAdmin;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

@OdmsController
public class ClinicianController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;

    public ClinicianController(DBManager manager) throws SQLException {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        if (!handler.getExists(driver.getConnection(), Clinician.class, "0")) {
            Clinician clinician = new Clinician("0", "admin", "default", "", "");
            handler.saveClinician(clinician, driver.getConnection());
        }
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians")
    public Collection<Clinician> getClinicians(@RequestParam("startIndex") int startIndex,
                                               @RequestParam("count") int count,
                                               @RequestParam(value = "q", required = false) String name,
                                               @RequestParam(value = "region", required = false) String region) {
        try (Connection connection = driver.getConnection()) {
            return handler.loadClinicians(connection, startIndex, count, name, region);
        } catch (SQLException ex) {
            Log.severe("Could not get clinicians", ex);
            throw new ServerDBException(ex);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/{staffId}")
    public Clinician getClinician(@PathVariable("staffId") String staffId) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            Clinician result = handler.getOneClinician(connection, staffId);
            if (result != null) {
                return result;
            }
            Log.warning("clinician not found with staffId " + staffId);
            throw new NotFoundException();

        } catch (SQLException ex) {
            Log.severe("cannot get clinician", ex);
            throw new ServerDBException(ex);
        }
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.POST, value = "/clinicians")
    public ResponseEntity postClinician(@RequestBody Clinician newClinician) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            handler.saveClinician(newClinician, connection);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        } catch (SQLException ex) {
            Log.severe("cannot add new clinician to database ", ex);
            throw new ServerDBException(ex);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/clinicians/{staffId}")
    public ResponseEntity putClinician(@PathVariable("staffId") String staffId, @RequestBody Clinician clinician) {
        try (Connection connection = driver.getConnection()) {
            handler.updateClinician(connection, staffId, clinician);
        } catch (SQLException ex) {
            Log.severe("cannot put clinician " + staffId, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.DELETE, value = "/clinicians/{staffId}")
    public ResponseEntity deleteClinician(@PathVariable("staffId") String staffId) {
        try (Connection connection = driver.getConnection()) {
            handler.deleteClinician(connection, staffId);
        } catch (SQLException ex) {
            Log.severe("cannot delete clinician " + staffId, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/clinicians/exists/{staffId}")
    public boolean getExists(@PathVariable("staffId") String staffId) {
        try (Connection connection = driver.getConnection()) {
            return handler.getExists(connection, Clinician.class, staffId);
        } catch (SQLException ex) {
            Log.severe("cannot check whether clinician exists", ex);
            throw new ServerDBException(ex);
        }
    }

}
