package odms.controller;

import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.jline.utils.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.SQLException;

@OdmsController
public class OrgansController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;


    public OrgansController(DBManager manager) {
        super(manager);
        this.driver = super.getDriver();
        this.handler = super.getHandler();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value="/availableOrgans")
    public List<> getAvailableOrgans(@RequestParam("startIndex") int startIndex,
                                     @RequestParam("count") int count,
                                     @RequestParam("organ") String organ,
                                     @RequestParam("region") String region){
        try(Connection connection = driver.getConnection()){
            return handler.getAvailableOrgans(startIndex, count, organ, region);
        } catch (SQLException e) {
            Log.error("Unable to retrieve organs from Db", e);
            throw new ServerDBException(e);
        }
    }
}
