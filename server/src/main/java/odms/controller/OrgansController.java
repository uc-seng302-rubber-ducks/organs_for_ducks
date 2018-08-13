package odms.controller;

import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
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
import java.util.HashMap;
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
            return handler.getAvailableOrgans(startIndex, count, organ, region, bloodType, city, country, connection);
        } catch (SQLException e) {
            Log.error("Unable to retrieve organs from Db", e);
            throw new ServerDBException(e);
        }
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.GET, value = "/matchingOrgans")
    public Map<AvailableOrganDetail, List<TransplantDetails>> getMatchingOrgans(@RequestParam(value = "startIndex") int startIndex,
                                                                                @RequestParam(value = "count") int count,
                                                                                @RequestParam(value = "matchesStartIndex") int matchesStartIndex,
                                                                                @RequestParam(value = "matchesCount") int matchesCount,
                                                                                @RequestParam(value = "organ", defaultValue = "", required = false) String organ,
                                                                          @RequestParam(value = "bloodType", defaultValue = "", required = false) String bloodType,
                                                                          @RequestParam(value = "city", defaultValue = "", required = false) String city,
                                                                          @RequestParam(value = "region", defaultValue = "", required = false) String region,
                                                                          @RequestParam(value = "country", defaultValue = "", required = false) String country) {
        try (Connection connection = driver.getConnection()) {
            Log.info("Getting all matching organs");
            String[] organs = {organ};

            List<AvailableOrganDetail> availableOrganDetails = handler.getAvailableOrgans(startIndex, count, organ, region, bloodType, city, country, connection);
            List<TransplantDetails> transplantDetails = handler.getTransplantDetails(connection, 0, Integer.MAX_VALUE, "", region, organs);
            OrganRanker organRanker = new OrganRanker();
            Map<AvailableOrganDetail, List<TransplantDetails>> matches = organRanker.matchOrgansToReceivers(availableOrganDetails, transplantDetails);
            Map<AvailableOrganDetail, List<TransplantDetails>> sortedMatches = new HashMap<>();
            while(matches.entrySet().iterator().hasNext()){
                Map.Entry<AvailableOrganDetail, List<TransplantDetails>> value = matches.entrySet().iterator().next();
                List<TransplantDetails> sortedTransplantDetails = OrganSorter.sortOrgansIntoRankedOrder(value.getKey(), value.getValue());
                List<TransplantDetails> sortedAndSizedTransplantDetails = new ArrayList<>();

                for(int i=matchesStartIndex; i <matchesCount; i++ ){
                    sortedAndSizedTransplantDetails.add(sortedTransplantDetails.get(i));
                }
                sortedMatches.put(value.getKey(), sortedAndSizedTransplantDetails);
            }
            return sortedMatches;
        } catch (SQLException e) {
            Log.error("Unable to retrieve matching organs from Db", e);
            throw new ServerDBException(e);
        }
    }
}
