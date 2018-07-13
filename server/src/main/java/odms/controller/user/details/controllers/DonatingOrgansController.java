package odms.controller.user.details.controllers;

import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.utils.DBHandler;
import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.controller.OdmsController;
import odms.exception.ServerDBException;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@OdmsController
public class DonatingOrgansController extends BaseController {
    private JDBCDriver driver;
    private DBHandler handler;

    public DonatingOrgansController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/donating")
    public ResponseEntity postDonatingOrgans(@PathVariable String nhi,
                                             @RequestBody Set<Organs> donating) {
        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            for (Organs organ : donating) {
                toModify.getDonorDetails().addOrgan(organ);
            }
            handler.saveUser(toModify, connection);
        } catch (SQLException ex) {
            Log.severe("Could not post donating organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/donating")
    public ResponseEntity putDonatingOrgans(@PathVariable String nhi,
                                            @RequestBody Set<Organs> donating) {
        try (Connection connection = driver.getConnection()) {
            User toModify = handler.getOneUser(connection, nhi);
            if (toModify == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            toModify.getDonorDetails().setOrgans(donating);
            handler.saveUser(toModify, connection);
        } catch (SQLException ex) {
            Log.severe("Could not put donating organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
