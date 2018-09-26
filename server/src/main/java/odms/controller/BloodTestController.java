package odms.controller;


import odms.commons.model._enum.EventTypes;
import odms.commons.model.datamodel.BloodTest;
import odms.commons.utils.Log;
import odms.database.BloodTestHandler;
import odms.database.DBHandler;
import odms.database.JDBCDriver;
import odms.exception.BadRequestException;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.socket.SocketHandler;
import odms.utils.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
                                               @RequestParam(value = "endDate", required = false) String endDateS,
                                               @RequestParam(value = "count", required = false) int count,
                                               @RequestParam(value = "startIndex", required = false) int startIndex) {
        LocalDate startDate;
        LocalDate endDate;

        try {
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = LocalDate.parse(startDateS, formatter);
            endDate = LocalDate.parse(endDateS, formatter);
        } catch (DateTimeException e) {
            Log.severe("Could not parse date for blood test " + startDateS + " " + endDateS, e);
            throw new BadRequestException();
        }
        try (Connection connection = driver.getConnection()) {
            return bloodTestHandler.getBloodTests(connection, nhi, startDate, endDate, count, startIndex);
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
    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/user/{nhi}/bloodTest")
    public ResponseEntity postBloodTest(@PathVariable(value ="nhi") String nhi,
                                        @RequestBody BloodTest bloodTest) throws IOException {
        try (Connection connection = driver.getConnection()) {
            bloodTestHandler.postBloodTest(connection, bloodTest, nhi);

            int id = bloodTestHandler.getNewBloodTestId(connection);
            socketHandler.broadcast(EventTypes.BLOOD_TEST_UPDATE, Integer.toString(id), Integer.toString(id));
            return new ResponseEntity(HttpStatus.CREATED);
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
    @IsClinician
    @RequestMapping(method = RequestMethod.PATCH, value = "/user/{nhi}/bloodTest/{id}")
    public ResponseEntity patchBloodTest(@PathVariable(value ="nhi") String nhi,
                                         @PathVariable(value = "id") int id,
                                         @RequestBody BloodTest bloodTest) throws IOException {
        try (Connection connection = driver.getConnection()) {
            bloodTestHandler.patchBloodTest(connection, nhi, id, bloodTest);
            socketHandler.broadcast(EventTypes.BLOOD_TEST_UPDATE, Integer.toString(bloodTest.getBloodTestId()), Integer.toString(id));
            return new ResponseEntity(HttpStatus.OK);
        } catch (SQLException e) {
            Log.severe("Could not patch a blood test", e);
            throw new ServerDBException(e);
        }
    }

    /**
     * Deletes a single blood test for a user
     *
     * @param nhi users nhi
     * @param id the id of the blood test to patch
     * @return the blood test
     */
    @IsClinician
    @RequestMapping(method = RequestMethod.DELETE, value = "/user/{nhi}/bloodTest/{id}")
    public ResponseEntity deleteBloodTest(@PathVariable(value ="nhi") String nhi,
                                          @PathVariable(value = "id") int id) throws IOException {
        try (Connection connection = driver.getConnection()) {
            bloodTestHandler.deleteBloodTest(connection, nhi, id);
            socketHandler.broadcast(EventTypes.BLOOD_TEST_UPDATE, Integer.toString(id), Integer.toString(id));
            return new ResponseEntity(HttpStatus.OK);
        } catch (SQLException e) {
            Log.severe("Could not patch a blood test", e);
            throw new ServerDBException(e);
        }
    }

}
