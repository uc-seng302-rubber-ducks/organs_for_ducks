package odms.controller.user.details;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.Disease;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.controller.OdmsController;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@OdmsController
public class DiseaseController extends BaseController {
    private DBHandler handler;
    private JDBCDriver driver;

    public DiseaseController(DBManager manager) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/diseases")
    public ResponseEntity postDisease(@PathVariable("nhi") String nhi,
                                      @RequestBody Disease disease) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            addDiseaseToCorrectList(disease, user.getPastDiseases(), user.getCurrentDiseases());
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Cannot add diseases to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/diseases")
    public ResponseEntity putDiseases(@PathVariable("nhi") String nhi,
                                      @RequestBody List<Disease> diseases) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            user.setCurrentDiseases(diseases.stream().filter(disease -> !disease.getIsCured()).collect(Collectors.toList()));
            user.setPastDiseases(diseases.stream().filter(Disease::getIsCured).collect(Collectors.toList()));
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to update the medications for user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Determines if a disease belongs to current or past diseases and adds it to the appropriate list
     *
     * @param disease disease to determine
     * @param past    Collection to add to if the disease was cured in the past
     * @param current Collection to add to if the disease is being treated
     */
    private void addDiseaseToCorrectList(Disease disease, Collection<Disease> past, Collection<Disease> current) {
        if (disease.getIsCured()) {
            past.add(disease);
        } else {
            current.add(disease);
        }
    }
}
