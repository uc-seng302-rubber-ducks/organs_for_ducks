package odms.controller;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.OrganSorter;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import odms.utils.OrganRanker;
import org.jline.utils.Log;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    @RequestMapping(method = RequestMethod.GET, value = "/availableOrgans")
    public List<AvailableOrganDetail> getAvailableOrgans(@RequestParam(value = "startIndex") int startIndex,
                                                         @RequestParam(value = "count") int count,
                                                         @RequestParam(value = "organ", defaultValue = "", required = false) String organ,
                                                         @RequestParam(value = "region", defaultValue = "", required = false) String region,
                                                         @RequestParam(value = "bloodType", defaultValue = "", required = false) String bloodType,
                                                         @RequestParam(value = "city", defaultValue = "", required = false) String city,
                                                         @RequestParam(value = "country", defaultValue = "", required = false) String country) {
        try (Connection connection = driver.getConnection()) {
            Log.info("Getting all available organs");
            return handler.getAvailableOrgans(startIndex, count, organ,  bloodType, region,connection);
        } catch (SQLException e) {
            Log.error("Unable to retrieve organs from Db", e);
            throw new ServerDBException(e);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/matchingOrgans")
    public List<TransplantDetails> getMatchingOrgans(@RequestParam(value = "startIndex") int startIndex,
                                                                                @RequestParam(value = "count") int count,
                                                                                @RequestParam(value = "donorNhi") String donorNhi,
                                                                                @RequestParam(value = "organ") String organ) {
        try (Connection connection = driver.getConnection()) {
            Log.info("Getting all matching organs");
            List<TransplantDetails> sortedMatches = new ArrayList<>();

            AvailableOrganDetail availableOrganDetail = handler.getAvailableOrgansByNhi(organ.toString(), donorNhi, connection);

            if(availableOrganDetail == null){
                return sortedMatches;
            }
            String[] organs = {organ};
            List<TransplantDetails> transplantDetails = handler.getTransplantDetails(connection,0, Integer.MAX_VALUE, "" , "", organs);
            OrganRanker organRanker = new OrganRanker();
            Map<AvailableOrganDetail, List<TransplantDetails>> matches = organRanker.matchOrgansToReceivers(availableOrganDetail, transplantDetails);
            for (Map.Entry<AvailableOrganDetail, List<TransplantDetails>> value : matches.entrySet()) {
                List<TransplantDetails> sortedTransplantDetails = OrganSorter.sortOrgansIntoRankedOrder(value.getKey(), value.getValue());
                List<TransplantDetails> sortedAndSizedTransplantDetails = new ArrayList<>();

                for (int i = startIndex; i < count; i++) {
                    if (i >= sortedTransplantDetails.size()) {
                        break;
                    }
                    sortedAndSizedTransplantDetails.add(sortedTransplantDetails.get(i));
                }
                return sortedAndSizedTransplantDetails;
            }
            return sortedMatches;
        } catch (SQLException e) {
            Log.error("Unable to retrieve matching organs from Db", e);
            throw new ServerDBException(e);
        }
    }
}
