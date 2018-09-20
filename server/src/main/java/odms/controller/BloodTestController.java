package odms.controller;


import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.BadRequestException;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Collection;

@OdmsController
public class BloodTestController extends BaseController {

    private final DBHandler handler;
    private final JDBCDriver driver;
    private final SocketHandler socketHandler;

    public BloodTestController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        this.socketHandler = socketHandler;
    }

    /**
     * Gets a single blood test for a user
     *
     * @param nhi users nhi
     * @param id blood test id
     * @return the blood test
     */
    @RequestMapping(method = RequestMethod.GET, value = "/user/{nhi}/bloodTest/{id}")
    public BloodTest getBloodTest(@PathVariable(value ="nhi") String nhi,
                                  @PathVariable(value = "id") int id){
        try(Connection connection = driver.getConnection() ){
            return handler.getBloodTest(connection, nhi, id);
        } catch (SQLException e) {
            Log.severe("Could not get a single blood test", e);
            throw new ServerDBException(e);
        }
    }


    /**
     * Gets all blood tests for a User.
     * Filters by date are optional
     *
     * @param nhi users nhi
     * @param startDateS date to start the results
     * @param endDateS date to end the results
     * @return results found by the server
     */
    @RequestMapping(method = RequestMethod.GET, value = "/user/{nhi}/bloodTests")
    public Collection<BloodTest> getBloodTests(@PathVariable(value ="nhi") String nhi,
                                               @RequestParam(value = "startDate", required = false) String startDateS,
                                               @RequestParam(value = "endDate", required = false) String endDateS){
        LocalDate startDate;
        LocalDate endDate;

        try{
            startDate = LocalDate.parse(startDateS);
            endDate = LocalDate.parse(endDateS);
        } catch (DateTimeException e){
            throw new BadRequestException();
        }
        try(Connection connection = driver.getConnection() ){
            return handler.getBloodTests(connection, nhi, startDate, endDate);
        } catch (SQLException e) {
            Log.severe("Could not get a blood tests", e);
            throw new ServerDBException(e);
        }
    }


    /**
     * Posts a single blood test for a user
     *
     * @param nhi users nhi
     * @param bloodTest test to put
     * @return the blood test
     */
    @RequestMapping(method = RequestMethod.POST, value = "/user/{nhi}/bloodTest")
    public BloodTest postBloodTest(@PathVariable(value ="nhi") String nhi,
                                  @RequestBody BloodTest bloodTest){
        try(Connection connection = driver.getConnection() ){
            return handler.postBloodTest(connection, nhi, bloodTest);
        } catch (SQLException e) {
            Log.severe("Could not post a blood test", e);
            throw new ServerDBException(e);
        }
    }

    /**
     * Posts a single blood test for a user
     *
     * @param nhi users nhi
     * @param id the id of the blood test to patch
     * @param bloodTest test to put
     * @return the blood test
     */
    @RequestMapping(method = RequestMethod.PATCH, value = "/user/{nhi}/bloodTest/{id}")
    public BloodTest patchBloodTest(@PathVariable(value ="nhi") String nhi,
                                   @PathVariable(value ="id") String id,
                                   @RequestBody BloodTest bloodTest){
        try(Connection connection = driver.getConnection() ){
            return handler.patchBloodTest(connection, nhi, id, bloodTest);
        } catch (SQLException e) {
            Log.severe("Could not patch a blood test", e);
            throw new ServerDBException(e);
        }
    }

}
