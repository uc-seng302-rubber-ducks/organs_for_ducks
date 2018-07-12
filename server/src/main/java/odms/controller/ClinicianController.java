package odms.controller;

import odms.commons.model.Clinician;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.utils.DBManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

}
