package odms.controller.user.details.controllers;

import odms.commons.model.Change;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;
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
import java.util.ArrayList;
import java.util.EnumMap;
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

    @RequestMapping(method = RequestMethod.POST, value = "/users/{nhi}/receiving")
    public ResponseEntity postOrgansToReceive(@RequestBody Map<String, ArrayList<ReceiverOrganDetailsHolder>> receiving,
                                              @PathVariable String nhi) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            for (Organs organ : Organs.values()) {
                if (receiving.containsKey(organ.toString())) {
                    user.getReceiverDetails().getOrgans().put(organ, receiving.get(organ.toString()));
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

    @RequestMapping(method = RequestMethod.PUT, value = "/users/{nhi}/receiving")
    public ResponseEntity putOrganToReceive(@RequestBody Map<String, ArrayList<ReceiverOrganDetailsHolder>> receiving,
                                            @PathVariable String nhi) {
        try (Connection connection = driver.getConnection()) {
            User user = handler.getOneUser(connection, nhi);
            if (user == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> map = new EnumMap<>(Organs.class);
            for (Map.Entry organSet : receiving.entrySet()) {
                for (Organs value : Organs.values()) {
                    if (value.toString().equals(organSet.getKey())) {
                        map.put(value, receiving.get(organSet.getKey().toString()));
                    }
                }
            }
            user.getReceiverDetails().setOrgans(map);
            user.addChange(new Change("Add organs to be received"));
            handler.saveUser(user, connection);
        } catch (SQLException ex) {
            Log.severe("Failed to post receiving organs to user " + nhi, ex);
            throw new ServerDBException(ex);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }


}
