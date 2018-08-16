package odms.controller.user.details;

import odms.commons.database.DBHandler;
import odms.commons.database.JDBCDriver;
import odms.commons.model.Change;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
import odms.commons.utils.Log;
import odms.controller.BaseController;
import odms.controller.OdmsController;
import odms.exception.ServerDBException;
import odms.security.IsClinician;
import odms.utils.DBManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@OdmsController
public class ReceivingOrgansController extends BaseController {

    private JDBCDriver driver;
    private DBHandler handler;

    public ReceivingOrgansController(DBManager manager) {
        super(manager);
        driver = super.getDriver();
        handler = super.getHandler();
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/receiving")
    public ResponseEntity postOrgansToReceive(@RequestBody Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving,
                                              @PathVariable String nhi) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            for (Organs organ : Organs.values()) {
                if (receiving.containsKey(organ)) {
                    user.getReceiverDetails().getOrgans().put(organ, receiving.get(organ));
                }
            }
            user.addChange(new Change("Add organs to be received"));
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to post receiving organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @IsClinician
    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/receiving")
    public ResponseEntity putOrganToReceive(@RequestBody Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> receiving,
                                            @PathVariable String nhi) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            user.getReceiverDetails().setOrgans(receiving);
            user.addChange(new Change("Add organs to be received"));
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to post receiving organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }


}
