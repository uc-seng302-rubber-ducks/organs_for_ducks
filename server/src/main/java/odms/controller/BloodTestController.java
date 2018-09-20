package odms.controller;


import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import odms.database.BloodTestHandler;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.BadRequestException;
import odms.exception.ServerDBException;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

@OdmsController
public class BloodTestController extends BaseController {

    private final DBHandler handler;
    private final JDBCDriver driver;
    private final SocketHandler socketHandler;
    private final BloodTestHandler bloodTestHandler;

    @Autowired
    public BloodTestController(DBManager manager, SocketHandler socketHandler) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
        this.socketHandler = socketHandler;
        this.bloodTestHandler = handler.getBloodTestHandler();
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
                                  @PathVariable(value = "id") int id) {
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.getBloodTest(connection, nhi, id);
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
     * @param startDateS date to start the results in format dd/MM/yyyy
     * @param endDateS date to end the results in format dd/MM/yyyy
     * @return results found by the server
     */
    @RequestMapping(method = RequestMethod.GET, value = "/user/{nhi}/bloodTests")
    public Collection<BloodTest> getBloodTests(@PathVariable(value ="nhi") String nhi,
                                               @RequestParam(value = "startDate", required = false) String startDateS,
                                               @RequestParam(value = "endDate", required = false) String endDateS) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("d/M/yyyy");
            startDate = LocalDate.parse(startDateS, formatter);
            endDate = LocalDate.parse(endDateS, formatter);
        } catch (DateTimeException e) {
            throw new BadRequestException();
        }
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.getBloodTests(connection, nhi, startDate, endDate);
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
                                   @RequestBody BloodTest bloodTest) {
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.postBloodTest(connection, bloodTest, nhi);
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
                                    @RequestBody BloodTest bloodTest) {
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.patchBloodTest(connection, nhi, id, bloodTest);
        } catch (SQLException e) {
            Log.severe("Could not patch a blood test", e);
            throw new ServerDBException(e);
        }
    }

    /**
     * deletes a single blood test for a user
     *
     * @param nhi users nhi
     * @param id the id of the blood test to patch
     * @return the blood test
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{nhi}/bloodTest/{id}")
    public BloodTest deleteBloodTest(@PathVariable(value ="nhi") String nhi,
                                     @PathVariable(value = "id") String id) {
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.deleteBloodTest(connection, nhi, id);
        } catch (SQLException e) {
            Log.severe("Could not patch a blood test", e);
            throw new ServerDBException(e);
        }
    }

}
