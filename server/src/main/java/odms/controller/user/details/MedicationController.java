package odms.controller.user.details;

import odms.commons.model.User;
import odms.commons.model.datamodel.Medication;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@OdmsController
public class MedicationController extends BaseController {

    private DBHandler handler;
    private JDBCDriver driver;

    public MedicationController(DBManager manager) {
        super(manager);
        handler = super.getHandler();
        driver = super.getDriver();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/medications")
    public ResponseEntity postMedication(@PathVariable("nhi") String nhi,
                                         @RequestBody Medication medication) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            addMedicationToCorrectList(medication, user.getPreviousMedication(), user.getCurrentMedication());
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Cannot add medications to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/medications")
    public ResponseEntity putMedications(@PathVariable("nhi") String nhi,
                                         @RequestBody List<Medication> medications) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            Collection<Medication> currentMedications = new HashSet<>();
            Collection<Medication> previousMedications = new HashSet<>();
            for (Medication medication : medications) {
                addMedicationToCorrectList(medication, previousMedications, currentMedications);
            }
            user.setCurrentMedication(new ArrayList<>(currentMedications));
            user.setPreviousMedication(new ArrayList<>(previousMedications));
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to update the medications for user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * Determines if a medication belongs to current or past medications and adds it to the appropriate list
     *
     * @param medication medication to determine
     * @param past       Collection to add to if the medication was taken in the past
     * @param current    Collection to add to if the medication is being taken
     */
    private void addMedicationToCorrectList(Medication medication, Collection<Medication> past, Collection<Medication> current) {
        if (medication.getMedicationTimes().size() % 2 == 0) {
            past.add(medication);
        } else {
            current.add(medication);
        }
    }
}
