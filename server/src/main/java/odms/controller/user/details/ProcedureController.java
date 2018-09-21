package odms.controller.user.details;

import odms.commons.model.MedicalProcedure;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.controller.OdmsController;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@OdmsController
public class ProcedureController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;

    public ProcedureController(DBManager manager) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/procedures")
    public ResponseEntity postMedicalProcedure(@PathVariable("nhi") String nhi,
                                               @RequestBody MedicalProcedure procedure) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            user.addMedicalProcedure(procedure);
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Cannot get user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/procedures")
    public ResponseEntity putProcedure(@PathVariable("nhi") String nhi,
                                       @RequestBody List<MedicalProcedure> procedures) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            user.setMedicalProcedures(procedures);
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to update the medical procedures for user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
