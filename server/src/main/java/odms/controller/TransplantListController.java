package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.Log;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@OdmsController
public class TransplantListController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;

    public TransplantListController(DBManager manager) {
        super(manager);
        this.driver = super.getDriver();
        this.handler = super.getHandler();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/transplantList")
    public List<TransplantDetails> getWaitingFor(@RequestParam("startIndex") int startIndex,
                                                 @RequestParam("count") int count,
                                                 @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                                 @RequestParam(value = "region", required = false, defaultValue = "") String region,
                                                 @RequestParam(value = "organs", required = false) String[] organs) {
        List<TransplantDetails> results;
        try (Connection connection = driver.getConnection()) {
            results = handler.getTransplantDetails(connection, startIndex, count, name, region, organs);
        } catch (SQLException ex) {
            Log.warning("failed to connect to database", ex);
            throw new ServerDBException();
        }
        if (results != null) {
            return results;
        }
        return new ArrayList<>();
    }
}
