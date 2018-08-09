package odms.controller;

import odms.commons.model.datamodel.AvailableOrganDetail;
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
import java.util.List;

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
    public List<AvailableOrganDetail> getAvailableOrgans(@RequestParam(value = "startIndex") int startIndex,
                                                         @RequestParam(value = "count") int count,
                                                         @RequestParam(value = "organ", required = false) String organ,
                                                         @RequestParam(value = "region", required = false) String region,
                                                         @RequestParam(value = "bloodType", required = false) String bloodType,
                                                         @RequestParam(value = "city", required = false) String city,
                                                         @RequestParam(value = "country", required = false) String country){
        try(Connection connection = driver.getConnection()){
            return handler.getAvailableOrgans(startIndex, count, organ, region, bloodType, city, country, connection);
        } catch (SQLException e) {
            Log.error("Unable to retrieve organs from Db", e);
            throw new ServerDBException(e);
        }
    }
}
