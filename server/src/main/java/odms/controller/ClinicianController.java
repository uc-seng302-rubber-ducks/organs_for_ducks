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

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

@OdmsController
public class ClinicianController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;

    public ClinicianController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }

    @IsAdmin
    @RequestMapping(method = RequestMethod.GET, value = "/clinicians")
    public Collection<Clinician> getClinicians(@RequestParam("startIndex") int startIndex,
                                               @RequestParam("count") int count,
                                               @RequestParam(value = "q", required = false) String name,
                                               @RequestParam(value = "region", required = false) String region) {
        try (Connection connection = driver.getConnection()) {
            return handler.loadClinicians(connection, startIndex, count);
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
    public ResponseEntity deleteClinician(@PathVariable("staffId") String staffId) throws SQLException {
        try (Connection connection = driver.getConnection()) {
            handler.deleteClinician(connection, staffId);
        } catch (SQLException ex) {
            Log.severe("cannot delete clinician " + staffId, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/clinician/{staffId}/photo")
    public byte[] getUserProfilePicture(@PathVariable("staffId") String staffId) {
        byte[] image;
        try (Connection connection = driver.getConnection()) {
            image = handler.getProfilePhoto(Clinician.class, staffId, connection);
        } catch (SQLException e) {
            Log.severe("Cannot fetch profile picture for user " + staffId, e);
            throw new ServerDBException(e);
        }
        return image;
    }
}
